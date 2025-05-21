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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
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
        System.out.print(request.getPhoneno()+"lalsl");
        System.out.println(request);
        Booking booking = bookingMapper.toBooking(request);
        booking.setCarIdcar(carIdcar);
        booking.setDriversinformation(request.getName()+","+request.getPhoneno()+","+request.getEmail()+","+request.getDrivinglicense());
        Car car = carRepository.findById(carIdcar).orElseThrow(() -> new AppException(ErrorCode.CAR_NOTFOUND));
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
            booking.setStatus(BookingStatus.PENDING_DEPOSIT.getStatus());
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

        Booking booking =
                bookingRepository.findById(idbooking).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOTFOUND));
        User user = userRepository
                .findById(booking.getUserIduser())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        Car car = carRepository
                .findById(booking.getCarIdcar())
                .orElseThrow(() -> new AppException(ErrorCode.CAR_NOTFOUND));
        User carowner = userRepository
                .findById(booking.getCarIdcarowner())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        Transactions transactionsCustomer = new Transactions();
        transactionsCustomer.setBookingno(booking.getBookingno());
        transactionsCustomer.setUserIduser(user.getIduser());
        transactionsCustomer.setType("Paid Deposit");
        transactionsCustomer.setDatetime(LocalDateTime.now());
        transactionsCustomer.setCarname(car.getName());

        Transactions transactionsCarOwner = new Transactions();
        transactionsCarOwner.setBookingno(booking.getBookingno());
        transactionsCarOwner.setUserIduser(carowner.getIduser());
        transactionsCarOwner.setType("Receive Deposit");
        transactionsCarOwner.setDatetime(LocalDateTime.now());
        transactionsCarOwner.setCarname(car.getName());

        if (booking.getStatus().equals(BookingStatus.PENDING_DEPOSIT.getStatus())) {
            System.out.println("Booking status: " + booking.getStatus());
            System.out.println("Payment method: " + booking.getPaymentmethod());
            if (booking.getPaymentmethod().equals(PayMentMethod.WALLET.getName())) {
                System.out.println("Payment method: " + booking.getPaymentmethod());
                System.out.println("User wallet: " + user.getWallet());
                user.setWallet(user.getWallet() - car.getDeposite());
                System.out.println("User wallet after deduction: " + user.getWallet());
                transactionsCustomer.setAmount(-car.getDeposite());
                booking.setStatus(BookingStatus.CONFIRMRED.getStatus());
                car.setStatus("Booked");

                carowner.setWallet(carowner.getWallet() + car.getDeposite());
                transactionsCarOwner.setAmount(car.getDeposite());
                transactionsRepository.save(transactionsCustomer);
                transactionsRepository.save(transactionsCarOwner);
            }
        }else{
            booking.setStatus(BookingStatus.PENDING_DEPOSIT.getStatus());
        }
        bookingRepository.save(booking);
        userRepository.save(user);
        userRepository.save(carowner);

        System.out.println("Booking status after payment: " + booking.getStatus());
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