
//package datn.datnbe.Service;
//
//import datn.datnbe.Entity.Booking;
//import datn.datnbe.Repository.BookingRepository;
//import datn.datnbe.Repository.UserRepository;
//import datn.datnbe.dto.response.ViewBookingListResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class ViewBookingListService {
//
//    @Autowired
//    private BookingRepository bookingRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    public List<ViewBookingListResponse> getBookingsForUser(String email) {
//        int userId = userRepository.findByEmail(email).get().getIduser();
//        List<Booking> bookings = bookingRepository.findByUserIduser(userId);
//        return bookings.stream().map(this::mapToBookingResponse).collect(Collectors.toList());
//    }
//
//    public ViewBookingListResponse getBookingById(Integer id) {
//        Optional<Booking> booking = bookingRepository.findById(id);
//        return booking.map(this::mapToBookingResponse).orElse(null);
//    }
//
//    private ViewBookingListResponse mapToBookingResponse(Booking booking) {
//        ViewBookingListResponse response = new ViewBookingListResponse();
//        response.setIdbooking(booking.getIdbooking());
//        response.setBookingno(booking.getBookingno());
//        response.setStartdatetime(booking.getStartdatetime());
//        response.setEnddatetime(booking.getEnddatetime());
//        response.setDriversinformation(booking.getDriversinformation());
//        response.setPaymentmethod(booking.getPaymentmethod());
//        response.setStatus(booking.getStatus());
//        response.setCarIdcar(booking.getCarIdcar());
//        response.setCarIdcarowner(booking.getCarIdcarowner());
//        response.setUserIduser(booking.getUserIduser());
//        return response;
//    }
//}

package datn.datnbe.Service;

import datn.datnbe.Entity.Booking;
import datn.datnbe.Entity.Car;
import datn.datnbe.Entity.User;
import datn.datnbe.Repository.BookingRepository;
import datn.datnbe.Repository.CarRepository;
import datn.datnbe.Repository.UserRepository;
import datn.datnbe.dto.response.ViewBookingListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ViewBookingListService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    public List<ViewBookingListResponse> getBookingsForUser(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && "CUSTOMER".equals(userOptional.get().getRole())) {
            int userId = userOptional.get().getIduser();
            List<Booking> bookings = bookingRepository.findByUserIduser(userId);
            return bookings.stream().map(this::mapToBookingResponse).collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("User không có quyền truy cập.");
        }
    }

    public ViewBookingListResponse getBookingById(Integer id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.map(this::mapToBookingResponse).orElse(null);
    }

    private ViewBookingListResponse mapToBookingResponse(Booking booking) {
        ViewBookingListResponse response = new ViewBookingListResponse();
        Optional<Car> optionalCar = carRepository.findById(booking.getCarIdcar());
        Car car = optionalCar.get();
        response.setIdbooking(booking.getIdbooking());
        response.setBookingno(booking.getBookingno());
        response.setStartdatetime(booking.getStartdatetime());
        response.setEnddatetime(booking.getEnddatetime());
        response.setDriversinformation(booking.getDriversinformation());
        response.setPaymentmethod(booking.getPaymentmethod());
        response.setStatus(booking.getStatus());
        response.setCarIdcar(booking.getCarIdcar());
        response.setCarImage(car.getImages());
        response.setCarName(car.getName());
        response.setCarIdcarowner(booking.getCarIdcarowner());
        response.setUserIduser(booking.getUserIduser());
        return response;
    }
}