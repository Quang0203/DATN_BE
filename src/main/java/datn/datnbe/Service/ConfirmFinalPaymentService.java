package datn.datnbe.Service;

import datn.datnbe.Entity.Booking;
import datn.datnbe.Entity.Car;
import datn.datnbe.Entity.Transactions;
import datn.datnbe.Entity.User;
import datn.datnbe.Enum.BookingStatus;
import datn.datnbe.Enum.CarStatus;
import datn.datnbe.Enum.PayMentMethod;
import datn.datnbe.Exception.AppException;
import datn.datnbe.Exception.ErrorCode;
import datn.datnbe.Mapper.BookingMapper;
import datn.datnbe.Repository.BookingRepository;
import datn.datnbe.Repository.CarRepository;
import datn.datnbe.Repository.TransactionsRepository;
import datn.datnbe.Repository.UserRepository;
import datn.datnbe.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Builder
public class ConfirmFinalPaymentService {

    @Autowired
    CarRepository carRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    BookingMapper bookingMapper;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private TransactionsRepository transactionsRepository;

    @PreAuthorize("hasRole('CAROWNER')")
    public ApiResponse confirmFinalPayment(Integer idbooking){
        Booking booking = bookingRepository.findById(idbooking).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOTFOUND));
        User customer = userRepository.findById(booking.getUserIduser()).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        Car car = carRepository.findById(booking.getCarIdcar()).orElseThrow(() -> new AppException(ErrorCode.CAR_NOTFOUND));
        User carOwner = userRepository.findById(booking.getCarIdcarowner()).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        Transactions transactionsCarOwner = new Transactions();
        transactionsCarOwner.setBookingno(booking.getBookingno());
        transactionsCarOwner.setUserIduser(carOwner.getIduser());
        transactionsCarOwner.setDatetime(LocalDateTime.now());
        transactionsCarOwner.setCarname(car.getName());

        Transactions transactionsCustomer = new Transactions();
        transactionsCustomer.setBookingno(booking.getBookingno());
        transactionsCustomer.setUserIduser(customer.getIduser());
        transactionsCustomer.setDatetime(LocalDateTime.now());
        transactionsCustomer.setCarname(car.getName());

        if(booking.getStatus().equals(BookingStatus.PENDING_PAYMENT.getStatus()))
        {
            if(!booking.getPaymentmethod().equals(PayMentMethod.WALLET.getName())){
                // Tạo giao dịch cho khách hàng (chỉ ghi nhận đã thanh toán qua ngân hàng)
                transactionsCustomer.setCarname(car.getName());
                transactionsCustomer.setAmount(-car.getDeposite());
                transactionsCustomer.setNote("Ví không thay đổi");
                // Tạo giao dịch cho chủ xe
                transactionsCarOwner.setCarname(car.getName());
                transactionsCarOwner.setAmount(car.getDeposite());
                transactionsCarOwner.setNote("Ví không thay đổi");
                if(booking.getPaymentmethod().equals(PayMentMethod.BANK_TRANSFER.getName())) {
                    // Nếu thanh toán bằng chuyển khoản thì không thay đổi ví
                    transactionsCustomer.setType("Nhận tiền hoàn cọc bằng chuyển khoản");
                    transactionsCarOwner.setType("Hoàn tiền cọc bằng chuyển khoản");
                }
                else {
                    transactionsCustomer.setType("Nhận tiền hoàn cọc bằng tiền mặt");
                    transactionsCarOwner.setType("Hoàn tiền cọc bằng tiền mặt");
                }
            }else {
                transactionsCustomer.setType("Nhận tiền hoàn cọc qua ví");
                transactionsCarOwner.setType("Hoàn tiền cọc qua ví");

                transactionsCustomer.setCarname(car.getName());
                transactionsCustomer.setAmount(+car.getDeposite());
                customer.setWallet(customer.getWallet() + car.getDeposite());

                transactionsCarOwner.setCarname(car.getName());
                transactionsCarOwner.setAmount(-car.getDeposite());
                carOwner.setWallet(carOwner.getWallet() - car.getDeposite());
                userRepository.save(customer);
                userRepository.save(carOwner);
            }
            car.setStatus("Available");
            booking.setStatus(BookingStatus.COMPLETE.getStatus());
            bookingRepository.save(booking);
            transactionsRepository.save(transactionsCustomer);
            transactionsRepository.save(transactionsCarOwner);
        }
        else throw new AppException(ErrorCode.CAROWNER_CONFIRM);
        return new ApiResponse()
                .builder()
                .result(booking)
                .build();
    }
}
