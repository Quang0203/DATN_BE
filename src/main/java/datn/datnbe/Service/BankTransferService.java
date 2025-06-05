package datn.datnbe.Service;

import datn.datnbe.Entity.Booking;
import datn.datnbe.Entity.Car;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;
import java.time.temporal.ChronoUnit;

import static datn.datnbe.Enum.BookingStatus.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Builder
public class BankTransferService {
    CarRepository carRepository;
    BookingRepository bookingRepository;
    BookingMapper bookingMapper;
    UserRepository userRepository;
    VNPAYService vnpayService;

    public String paidDeposid(Integer idbooking) throws UnsupportedEncodingException {
        Booking booking =
                bookingRepository.findById(idbooking).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOTFOUND));
        Car car = carRepository
                .findById(booking.getCarIdcar())
                .orElseThrow(() -> new AppException(ErrorCode.CAR_NOTFOUND));
        if (booking.getStatus().equals(BookingStatus.INITIALIZED.getStatus())) {
            return vnpayService.createPayment(idbooking, car.getDeposite(), car);
        } else if (booking.getStatus().equals(IN_PROGRESS.getStatus())) {
            float dayBetween = (int) ChronoUnit.DAYS.between(booking.getStartdatetime(), booking.getEnddatetime());
            System.out.println("Day between: " + dayBetween);
            if (dayBetween < 1) dayBetween = 1;
//            float remaining = car.getBaseprice() * dayBetween - car.getDeposite();
            float remaining = car.getBaseprice() * dayBetween;
            return vnpayService.createPayment(idbooking, remaining, car);
        } else if (booking.getStatus().equals(PENDING_PAYMENT.getStatus())) {
            return vnpayService.createPayment(idbooking, car.getDeposite(), car);
        }
        else {
            throw new AppException(ErrorCode.BOOKING_NOTFOUND);
        }
    }
}