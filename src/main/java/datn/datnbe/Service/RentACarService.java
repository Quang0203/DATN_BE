package datn.datnbe.Service;

import datn.datnbe.Entity.Booking;
import datn.datnbe.Entity.Car;
import datn.datnbe.Entity.Transactions;
import datn.datnbe.Entity.User;
import datn.datnbe.Enum.BookingStatus;
import datn.datnbe.Enum.PayMentMethod;
import datn.datnbe.Exception.AppException;
import datn.datnbe.Exception.ErrorCode;
import datn.datnbe.Mapper.BookingMapper;
import datn.datnbe.Repository.BookingRepository;
import datn.datnbe.Repository.CarRepository;
import datn.datnbe.Repository.TransactionsRepository;
import datn.datnbe.Repository.UserRepository;
import datn.datnbe.dto.request.RentACarRequest;
import datn.datnbe.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Builder
public class RentACarService {
    CarRepository carRepository;
    BookingRepository bookingRepository;
    BookingMapper bookingMapper;
    UserRepository userRepository;
    VNPAYService vnpayService;
    TransactionsRepository transactionsRepository;
    private JavaMailSender javaMailSender;

    public class BookingNoGenerator {
        public static String generateBookingNo() {
            UUID uuid = UUID.randomUUID();
            String[] parts = uuid.toString().split("-");
            // Use just the first part of the UUID (which is highly unique)
            return parts[0];
        }
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse makeABooking(RentACarRequest request, int carIdcar) {
        System.out.print(request.getPhoneno() + "lalsl");
        System.out.println(request);
        Booking booking = bookingMapper.toBooking(request);
        booking.setCarIdcar(carIdcar);
        booking.setDriversinformation(request.getName() + "," + request.getPhoneno() + "," + request.getEmail() + "," + request.getDrivinglicense());
        Car car = carRepository.findById(carIdcar).orElseThrow(() -> new AppException(ErrorCode.CAR_NOTFOUND));
        car.setStatus("Booked");
        int idCarOwner = car.getIdcarowner();

        if (carRepository.checkCarAvailable(request.getStartdatetime(), request.getEnddatetime(), carIdcar) == 1) {
            booking.setCarIdcarowner(idCarOwner);
            var context = SecurityContextHolder.getContext();
            String email = context.getAuthentication().getName();
            User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

            // Kiểm tra số dư ví nếu phương thức thanh toán là "My wallet"
            if (PayMentMethod.WALLET.getName().equals(request.getPaymentmethod())) {
                double userBalance = user.getWallet();
                double carDeposit = car.getDeposite();
                if (userBalance < carDeposit) {
                    throw new AppException(ErrorCode.NOTENOUGH_WALLET);
                }
            }

            booking.setUserIduser(user.getIduser());
            booking.setStatus(BookingStatus.INITIALIZED.getStatus());
            booking.setEnddatetime(request.getEnddatetime());
            booking.setStartdatetime(request.getStartdatetime());
            booking.setBookingno(BookingNoGenerator.generateBookingNo());
            bookingRepository.save(booking);

            // Lấy thông tin chủ xe
            User carOwner = userRepository.findById(idCarOwner).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
            String ownerEmail = carOwner.getEmail();

            // Phân tích thông tin khách hàng
            String[] driverInfo = booking.getDriversinformation().split(",");
            String customerName = driverInfo[0];
            String customerPhone = driverInfo[1];

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // Định dạng LocalDateTime trực tiếp
            String startDateTime = booking.getStartdatetime().format(formatter);
            String endDateTime = booking.getEnddatetime().format(formatter);

            System.out.println("Start: " + startDateTime + ", End: " + endDateTime);

            // Gửi email thông báo
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(ownerEmail);
                message.setSubject("Thông Báo: Xe Của Bạn Đã Được Thuê");
                message.setText("Kính gửi Chủ Xe,\n\nKhách hàng " + customerName + " với số điện thoại " + customerPhone +
                        " đã thuê xe của bạn từ " + startDateTime + " đến " + endDateTime +
                        ".\n\nMã Booking: " + booking.getBookingno() +
                        "\n\nVui lòng liên hệ với khách hàng để sắp xếp việc thuê xe.\n\nTrân trọng.");
                javaMailSender.send(message);
            } catch (MailException e) {
                // Ghi log lỗi nhưng không làm gián đoạn quá trình tạo booking
                System.err.println("Không thể gửi email: " + e.getMessage());
            }

        } else throw new AppException(ErrorCode.CAR_NOT_AVAILABLE);

        return new ApiResponse()
                .builder()
                .result(booking)
                .build();
    }

    @Transactional
    public ApiResponse paidDeposid(Integer idbooking) throws UnsupportedEncodingException {
        Booking booking = bookingRepository.findById(idbooking).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOTFOUND));
        User user = userRepository.findById(booking.getUserIduser()).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        Car car = carRepository.findById(booking.getCarIdcar()).orElseThrow(() -> new AppException(ErrorCode.CAR_NOTFOUND));
        User carowner = userRepository.findById(booking.getCarIdcarowner()).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        if (booking.getStatus().equals(BookingStatus.INITIALIZED.getStatus())) {
            if (booking.getPaymentmethod().equals(PayMentMethod.WALLET.getName())) {
                // Xử lý thanh toán bằng ví
                user.setWallet(user.getWallet() - car.getDeposite());
                booking.setStatus(BookingStatus.CONFIRMED.getStatus());
                car.setStatus("Booked");
                // Tạo giao dịch cho khách hàng
                Transactions transactionsCustomer = new Transactions();
                transactionsCustomer.setBookingno(booking.getBookingno());
                transactionsCustomer.setUserIduser(user.getIduser());
                transactionsCustomer.setType("Trả tiền cọc");
                transactionsCustomer.setDatetime(LocalDateTime.now());
                transactionsCustomer.setCarname(car.getName());
                transactionsCustomer.setAmount(-car.getDeposite());
                transactionsRepository.save(transactionsCustomer);

                // Tạo giao dịch cho chủ xe
                carowner.setWallet(carowner.getWallet() + car.getDeposite());
                Transactions transactionsCarOwner = new Transactions();
                transactionsCarOwner.setBookingno(booking.getBookingno());
                transactionsCarOwner.setUserIduser(carowner.getIduser());
                transactionsCarOwner.setType("Nhận tiền cọc");
                transactionsCarOwner.setDatetime(LocalDateTime.now());
                transactionsCarOwner.setCarname(car.getName());
                transactionsCarOwner.setAmount(car.getDeposite());
                transactionsRepository.save(transactionsCarOwner);
            } else {
                if (booking.getPaymentmethod().equals(PayMentMethod.BANK_TRANSFER.getName())) {
                    // Xử lý thanh toán bằng chuyển khoản ngân hàng
                    booking.setStatus(BookingStatus.PENDING_DEPOSIT.getStatus());
                    // Tạo giao dịch cho khách hàng (chỉ ghi nhận đã thanh toán qua ngân hàng)
                    Transactions transactionsCustomer = new Transactions();
                    transactionsCustomer.setBookingno(booking.getBookingno());
                    transactionsCustomer.setUserIduser(user.getIduser());
                    transactionsCustomer.setType("Trả tiền cọc qua chuyển khoản ngân hàng");
                    transactionsCustomer.setDatetime(LocalDateTime.now());
                    transactionsCustomer.setCarname(car.getName());
                    transactionsCustomer.setAmount(-car.getDeposite());
                    transactionsCustomer.setNote("Ví không thay đổi");
                    transactionsRepository.save(transactionsCustomer);
                    // Tạo giao dịch cho chủ xe
                    Transactions transactionsCarOwner = new Transactions();
                    transactionsCarOwner.setBookingno(booking.getBookingno());
                    transactionsCarOwner.setUserIduser(carowner.getIduser());
                    transactionsCarOwner.setType("Nhận tiền cọc qua chuyển khoản ngân hàng");
                    transactionsCarOwner.setDatetime(LocalDateTime.now());
                    transactionsCarOwner.setCarname(car.getName());
                    transactionsCarOwner.setAmount(car.getDeposite());
                    transactionsCarOwner.setNote("Ví không thay đổi");
                    transactionsRepository.save(transactionsCarOwner);
                } else {
                    // Xử lý thanh toán bằng tiền mặt
                    booking.setStatus(BookingStatus.PENDING_DEPOSIT.getStatus());
                    // Tạo giao dịch cho khách hàng (chỉ ghi nhận đã thanh toán qua ngân hàng)
                    Transactions transactionsCustomer = new Transactions();
                    transactionsCustomer.setBookingno(booking.getBookingno());
                    transactionsCustomer.setUserIduser(user.getIduser());
                    transactionsCustomer.setType("Trả tiền cọc qua tiền mặt");
                    transactionsCustomer.setDatetime(LocalDateTime.now());
                    transactionsCustomer.setCarname(car.getName());
                    transactionsCustomer.setAmount(-car.getDeposite());
                    transactionsCustomer.setNote("Ví không thay đổi");
                    transactionsRepository.save(transactionsCustomer);
                    // Tạo giao dịch cho chủ xe
                    Transactions transactionsCarOwner = new Transactions();
                    transactionsCarOwner.setBookingno(booking.getBookingno());
                    transactionsCarOwner.setUserIduser(carowner.getIduser());
                    transactionsCarOwner.setType("Nhận tiền cọc băằng tiền mặt");
                    transactionsCarOwner.setDatetime(LocalDateTime.now());
                    transactionsCarOwner.setCarname(car.getName());
                    transactionsCarOwner.setAmount(car.getDeposite());
                    transactionsCarOwner.setNote("Ví không thay đổi");
                    transactionsRepository.save(transactionsCarOwner);
                }
            }
        }
        // Lưu các thay đổi
        bookingRepository.save(booking);
        userRepository.save(user);
        userRepository.save(carowner);
        carRepository.save(car);

        return new ApiResponse().builder().result(booking).build();
    }

    public ApiResponse confirmDeposit(Integer idbooking) {
        Booking booking = bookingRepository.findById(idbooking).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOTFOUND));
        if (booking.getStatus().equals(BookingStatus.PENDING_PAYMENT.getStatus()) || booking.getStatus().equals(BookingStatus.PENDING_DEPOSIT.getStatus())) {
            booking.setStatus(BookingStatus.CONFIRMED.getStatus());
            bookingRepository.save(booking);
        }
        return new ApiResponse().builder().result(booking).build();
    }

    public ApiResponse getCarById(Integer carIdcar) {
        Car car = carRepository.findById(carIdcar).orElseThrow(() -> new AppException(ErrorCode.CAR_NOTFOUND));
        return new ApiResponse().builder().result(car).build();
    }

    public ApiResponse getBooking(int id) {
        return new ApiResponse<>().builder().result(bookingRepository.findBookingByIdCar(id)).build();
    }

    public ApiResponse getListCar() {
        return new ApiResponse<>().builder().result(carRepository.findAll()).build();
    }
}