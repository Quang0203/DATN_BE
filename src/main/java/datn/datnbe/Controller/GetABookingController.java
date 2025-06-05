package datn.datnbe.Controller;

import datn.datnbe.Entity.Booking;
import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.Service.GetABookingService;
import datn.datnbe.dto.response.PaginatedResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/carowner/paginated")
    public PaginatedResponse<Booking> getListBookingPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return getABookingService.getListBookingPaginated(page, size);
    }

    @GetMapping("/user")
    public ApiResponse getListBookingUser() {
        return getABookingService.getListBookingUser();
    }
}
