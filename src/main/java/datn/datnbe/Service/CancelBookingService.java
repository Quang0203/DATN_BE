package datn.datnbe.Service;

import datn.datnbe.Enum.CarStatus;
import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.Entity.Booking;
import datn.datnbe.Entity.Car;
import datn.datnbe.Entity.User;
import datn.datnbe.Enum.BookingStatus;
import datn.datnbe.Exception.AppException;
import datn.datnbe.Exception.ErrorCode;
import datn.datnbe.Mapper.BookingMapper;
import datn.datnbe.Repository.BookingRepository;
import datn.datnbe.Repository.CarRepository;
import datn.datnbe.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Builder
public class CancelBookingService {
    @Autowired
    CarRepository carRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    BookingMapper bookingMapper;

    @Autowired
    UserRepository userRepository;

    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse cancelBooking(Integer idbooking){
        System.out.println(idbooking);
        Booking booking = bookingRepository.findById(idbooking).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOTFOUND));
        User user = userRepository.findById(booking.getUserIduser()).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        User carowner = userRepository.findById(booking.getCarIdcarowner()).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        Car car = carRepository.findById(booking.getCarIdcar()).orElseThrow(() -> new AppException(ErrorCode.CAR_NOTFOUND));
        if(booking.getStatus().equals(BookingStatus.CONFIRMED.getStatus()))
        {
//            user.setWallet(user.getWallet() + car.getDeposite());
//            carowner.setWallet(carowner.getWallet() - car.getDeposite());
//            userRepository.save(user);
//            userRepository.save(carowner);
            car.setStatus(CarStatus.Available.name());
            booking.setStatus(BookingStatus.CANCELLED.getStatus());
            bookingRepository.save(booking);
        }
        if(booking.getStatus().equals(BookingStatus.INITIALIZED.getStatus()))
        {
            car.setStatus(CarStatus.Available.name());
            booking.setStatus(BookingStatus.CANCELLED.getStatus());
            bookingRepository.save(booking);

        }
        if (booking.getStatus().equals(BookingStatus.PENDING_DEPOSIT.getStatus())) {
            car.setStatus(CarStatus.Available.name());
            booking.setStatus(BookingStatus.CANCELLED.getStatus());
            bookingRepository.save(booking);
        }

        List<User> resArray = new ArrayList<>();
        resArray.add(user);
        resArray.add(carowner);
        return new ApiResponse()
                .builder()
                .result(booking)
                .build();
    }
}
