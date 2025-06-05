package datn.datnbe.Controller;

import datn.datnbe.Service.ViewBookingListService;
import datn.datnbe.dto.response.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import datn.datnbe.dto.response.ViewBookingListResponse;

import java.util.List;

@RestController
@RequestMapping("/view-bookings")
public class ViewBookingListController {

    @Autowired
    private ViewBookingListService viewBookingListService;

//    @GetMapping
//    public List<ViewBookingListResponse> getBookingsForCurrentUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//        return viewBookingListService.getBookingsForUser(email);
//    }

    @GetMapping
    public PaginatedResponse<ViewBookingListResponse> getBookingsForCurrentUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return viewBookingListService.getBookingsForUserPaginated(email, page, size);
    }

    @GetMapping("/{id}")
    public ViewBookingListResponse getBookingById(@PathVariable Integer id) {
        return viewBookingListService.getBookingById(id);
    }
}
