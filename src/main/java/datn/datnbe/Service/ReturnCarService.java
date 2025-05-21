package datn.datnbe.Service;

import datn.datnbe.Entity.Booking;
import datn.datnbe.Entity.Car;
import datn.datnbe.Entity.Transactions;
import datn.datnbe.Entity.User;
import datn.datnbe.Enum.BookingStatus;
import datn.datnbe.Exception.AppException;
import datn.datnbe.Exception.ErrorCode;
import datn.datnbe.Mapper.BookingMapper;
import datn.datnbe.Repository.*;
import datn.datnbe.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Builder
public class ReturnCarService {

    @Autowired
    CarRepository carRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    BookingMapper bookingMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionsRepository transactionsRepository;

    //    @PreAuthorize("hasRole('CUSTOMER')")
//    public ApiResponse returnCar(Integer idbooking){
//        Booking booking = bookingRepository.findById(idbooking).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOTFOUND));
//        User user = userRepository.findById(booking.getUserIduser()).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
//        User carOwner = userRepository.findById(booking.getCarIdcarowner()).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
//        Car car = carRepository.findById(booking.getCarIdcar()).orElseThrow(() -> new AppException(ErrorCode.CAR_NOTFOUND));
//        if(booking.getStatus().equals(BookingStatus.IN_PROGRESS.getStatus()))
//        {
//            float remaining = car.getBaseprice() - car.getDeposite();
//            if (user.getWallet() < remaining){
//                booking.setStatus(BookingStatus.PENDING_PAYMENT.getStatus());
//                bookingRepository.save(booking);
//                throw new AppException(ErrorCode.NOTENOUGH_WALLET);
//            }
//            booking.setStatus(BookingStatus.COMPLETE.getStatus());
//            user.setWallet(user.getWallet() - remaining);
//            carOwner.setWallet(carOwner.getWallet() + remaining);
//            userRepository.save(user);
//            userRepository.save(carOwner);
//            bookingRepository.save(booking);
//        }
//        return new ApiResponse()
//                .builder()
//                .result(booking)
//                .build();
//    }
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse returnCar(Integer idbooking) {
        System.out.println("Returning car with booking ID: " + idbooking
        );
        Booking booking =
                bookingRepository.findById(idbooking).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOTFOUND));
        User user = userRepository
                .findById(booking.getUserIduser())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        User carOwner = userRepository
                .findById(booking.getCarIdcarowner())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        Car car = carRepository
                .findById(booking.getCarIdcar())
                .orElseThrow(() -> new AppException(ErrorCode.CAR_NOTFOUND));


        Transactions transactionsCustomer = new Transactions();
        transactionsCustomer.setBookingno(booking.getBookingno());
        transactionsCustomer.setUserIduser(user.getIduser());
        transactionsCustomer.setType("Final Payment");
        transactionsCustomer.setDatetime(LocalDateTime.now());
        transactionsCustomer.setCarname(car.getName());

        Transactions transactionsCarOwner = new Transactions();
        transactionsCarOwner.setBookingno(booking.getBookingno());
        transactionsCarOwner.setUserIduser(carOwner.getIduser());
        transactionsCarOwner.setType("Final Payment");
        transactionsCarOwner.setDatetime(LocalDateTime.now());
        transactionsCarOwner.setCarname(car.getName());

        if (booking.getStatus().equals(BookingStatus.IN_PROGRESS.getStatus())
                || booking.getStatus().equals(BookingStatus.PENDING_PAYMENT.getStatus())) {
            float dayBetween = (int) ChronoUnit.DAYS.between(booking.getStartdatetime(), booking.getEnddatetime());
            System.out.println("Day between: " + dayBetween);
            if (dayBetween < 1) dayBetween = 1;
            float remaining = car.getBaseprice() * dayBetween - car.getDeposite();
            if (user.getWallet() < remaining) {
                booking.setStatus(BookingStatus.PENDING_PAYMENT.getStatus());
                bookingRepository.save(booking);
                throw new AppException(ErrorCode.NOTENOUGH_WALLET);
            }
            booking.setStatus(BookingStatus.COMPLETE.getStatus());
            user.setWallet(user.getWallet() - remaining);
            transactionsCustomer.setAmount(-remaining);
            carOwner.setWallet(carOwner.getWallet() + remaining);
            transactionsCarOwner.setAmount(remaining);
            System.out.println("User wallet after deduction: " + user.getWallet());
            System.out.println("Car owner wallet after addition: " + carOwner.getWallet());

            car.setStatus("Available");
            transactionsRepository.save(transactionsCustomer);
            transactionsRepository.save(transactionsCarOwner);
            carRepository.save(car);
            userRepository.save(user);
            userRepository.save(carOwner);
            bookingRepository.save(booking);
        }
        return new ApiResponse().builder().result(booking).build();
    }
}
