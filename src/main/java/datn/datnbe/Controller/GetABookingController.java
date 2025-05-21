package datn.datnbe.Controller;

import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.Service.GetABookingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/getbooking")

public class GetABookingController {
    
    @Autowired
    GetABookingService getABookingService;

    @GetMapping("/{idbooking}")
    public ApiResponse getABooking(@PathVariable("idbooking") Integer idbooking){
        return new ApiResponse()
                .builder()
                .result(getABookingService.getABooking(idbooking))
                .build();
    }

    @GetMapping("/carowner")
    public ApiResponse getListBooking() {
        return getABookingService.getListBooking();
    }

    @GetMapping("/user")
    public ApiResponse getListBookingUser() {
        return getABookingService.getListBookingUser();
    }
}
