package datn.datnbe.Controller;

import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.Service.CancelBookingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
public class CancelBookingController {
    CancelBookingService cancelBookingService;

    @PostMapping("/cancelbooking/{idbooking}")
    public ApiResponse cancelBooking(@PathVariable("idbooking") Integer idbooking){

        return new ApiResponse()
                .builder()
                .result(cancelBookingService.cancelBooking(idbooking))
                .build();
    }
}
