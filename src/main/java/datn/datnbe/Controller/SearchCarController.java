package datn.datnbe.Controller;


import datn.datnbe.Entity.Booking;
import datn.datnbe.Repository.BookingRepository;
import datn.datnbe.Service.SearchCarService;
import datn.datnbe.dto.request.SearchCarRequest;
import datn.datnbe.dto.request.SearchCarRequestNew;
import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.dto.response.SearchCarResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/searchCar")
public class SearchCarController {

    @Autowired
    SearchCarService searchCarService;

    @Autowired
    BookingRepository bookingRepository;


    @PostMapping
    public ApiResponse<List<SearchCarResponse>> searchCar(@RequestBody SearchCarRequest searchCarRequest){
        return searchCarService.getListCar(searchCarRequest);
    }

    @PostMapping("/new")
    public ApiResponse<List<SearchCarResponse>> searchCarNew(@RequestBody SearchCarRequestNew searchCarRequest){
        return searchCarService.findAvailableCars(searchCarRequest);
    }

    /**
     * Lấy danh sách booking của một xe cụ thể
     * @param carId ID của xe
     * @return Danh sách booking của xe đó
     */
    @GetMapping("/bookingcar/{carId}")
    public ApiResponse<List<Booking>> getBookingsByCarId(@PathVariable Integer carId) {
        try {
            List<Booking> bookings = bookingRepository.findBookingByIdCar(carId);
            ApiResponse<List<Booking>> response = new ApiResponse<>();
            response.setResult(bookings);
            response.setMessage("Successfully retrieved bookings for car ID: " + carId);
            return response;
        } catch (Exception e) {
            ApiResponse<List<Booking>> response = new ApiResponse<>();
            response.setMessage("Error retrieving bookings: " + e.getMessage());
            return response;
        }
    }

}
