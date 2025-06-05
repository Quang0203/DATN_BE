package datn.datnbe.Service;


import datn.datnbe.Entity.User;
import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.Entity.Booking;
import datn.datnbe.Exception.AppException;
import datn.datnbe.Exception.ErrorCode;
import datn.datnbe.Mapper.BookingMapper;
import datn.datnbe.Repository.BookingRepository;
import datn.datnbe.Repository.CarRepository;
import datn.datnbe.Repository.UserRepository;
import datn.datnbe.dto.response.PaginatedResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Builder
public class GetABookingService {
    @Autowired
    CarRepository carRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    BookingMapper bookingMapper;

    @Autowired
    UserRepository userRepository;

    public ApiResponse getABooking(Integer idbooking){
        Booking booking = bookingRepository.findById(idbooking).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOTFOUND));
        return new ApiResponse()
                .builder()
                .result(booking)
                .build();
    }

    public ApiResponse getListBooking() {
        var context = SecurityContextHolder.getContext();
//        Jwt jwt = (Jwt) context.getAuthentication().getPrincipal();
//        var claims = jwt.getClaims();
//        Long longIdUser = (Long) claims.get("id");
//        int idUser = longIdUser.intValue();
//        User user = userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        List<Booking> booking = bookingRepository.findBookingByIdCarOwner(user.getIduser());
        return new ApiResponse<>().builder().result(booking).build();
    }

    public PaginatedResponse<Booking> getListBookingPaginated(int page, int size) {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        Pageable pageable = PageRequest.of(page, size, Sort.by("startdatetime").descending());
        Page<Booking> bookingPage = bookingRepository.findBookingByIdCarOwner(user.getIduser(), pageable);
        return new PaginatedResponse<>(
                bookingPage.getContent(),
                page,
                size,
                bookingPage.getTotalElements(),
                bookingPage.getTotalPages()
        );
    }

    public ApiResponse getListBookingUser() {
        var context = SecurityContextHolder.getContext();
//        Jwt jwt = (Jwt) context.getAuthentication().getPrincipal();
//        var claims = jwt.getClaims();
//        Long longIdUser = (Long) claims.get("id");
//        int idUser = longIdUser.intValue();
//        User user = userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        String email = context.getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        List<Booking> booking = bookingRepository.findBookingByIdUser(user.getIduser());
        return new ApiResponse<>().builder().result(booking).build();
    }

}
