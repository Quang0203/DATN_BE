package datn.datnbe.Service;

import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.Entity.Booking;
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

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Builder
public class ConfirmPickUpService {

    @Autowired
    CarRepository carRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    BookingMapper bookingMapper;

    @Autowired
    UserRepository userRepository;

    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse confirmPickUpService(Integer idbooking){
        Booking booking = bookingRepository.findById(idbooking).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOTFOUND));
        if(booking.getStatus().equals(BookingStatus.CONFIRMRED.getStatus()))
        {
            booking.setStatus(BookingStatus.IN_PROGRESS.getStatus());
            bookingRepository.save(booking);
        }
        return new ApiResponse()
                .builder()
                .result(booking)
                .build();
    }
}
