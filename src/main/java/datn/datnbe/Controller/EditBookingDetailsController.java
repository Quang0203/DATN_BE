package datn.datnbe.Controller;

import datn.datnbe.Service.EditBookingDetailsService;
import datn.datnbe.dto.request.EditBookingDetailsRequest;
import datn.datnbe.dto.response.ViewBookingListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/edit-booking")
public class EditBookingDetailsController {

    @Autowired
    private EditBookingDetailsService editBookingDetailsService;

    @PutMapping("/{id}")
    public ViewBookingListResponse updateBooking(@PathVariable Integer id, @RequestBody EditBookingDetailsRequest bookingRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return editBookingDetailsService.updateBooking(id, email, bookingRequest);
    }
}
