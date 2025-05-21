package datn.datnbe.Service;

import datn.datnbe.Entity.Booking;
import datn.datnbe.Entity.User;
import datn.datnbe.Repository.BookingRepository;
import datn.datnbe.Repository.UserRepository;
import datn.datnbe.dto.request.EditBookingDetailsRequest;
import datn.datnbe.dto.response.ViewBookingListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EditBookingDetailsService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    public ViewBookingListResponse updateBooking(Integer id, String email, EditBookingDetailsRequest bookingRequest) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && "CUSTOMER".equals(userOptional.get().getRole())) {
            int userId = userOptional.get().getIduser();
            Optional<Booking> existingBooking = bookingRepository.findById(id);
            if (existingBooking.isPresent()) {
                Booking booking = existingBooking.get();

                // Kiểm tra trạng thái đặt chỗ
                if (isEditable(booking.getStatus())) {
                    booking.setBookingno(bookingRequest.getBookingno());
                    booking.setStartdatetime(bookingRequest.getStartdatetime());
                    booking.setEnddatetime(bookingRequest.getEnddatetime());
                    booking.setDriversinformation(bookingRequest.getDriversinformation());
                    booking.setPaymentmethod(bookingRequest.getPaymentmethod());
                    booking.setStatus(bookingRequest.getStatus());
                    booking.setCarIdcar(bookingRequest.getCarIdcar());
                    booking.setCarIdcarowner(bookingRequest.getCarIdcarowner());
                    booking.setUserIduser(bookingRequest.getUserIduser());
                    bookingRepository.save(booking);
                    return mapToBookingResponse(booking);
                } else {
                    // Xử lý nếu không cho phép chỉnh sửa
                    throw new IllegalArgumentException("Không thể chỉnh sửa đặt chỗ với trạng thái hiện tại: " + booking.getStatus());
                }
            } else {
                return null; // hoặc bạn có thể ném ra một ngoại lệ phù hợp
            }
        } else {
            throw new IllegalArgumentException("User không có quyền truy cập.");
        }
    }

    private boolean isEditable(String status) {
        switch (status) {
            case "Confirmed":
            case "Pending deposit":
                return true;
            case "In-Progress":
            case "Cancelled":
            case "Pending payment":
            case "Completed":
            default:
                return false;
        }
    }

    private ViewBookingListResponse mapToBookingResponse(Booking booking) {
        ViewBookingListResponse response = new ViewBookingListResponse();
        response.setIdbooking(booking.getIdbooking());
        response.setBookingno(booking.getBookingno());
        response.setStartdatetime(booking.getStartdatetime());
        response.setEnddatetime(booking.getEnddatetime());
        response.setDriversinformation(booking.getDriversinformation());
        response.setPaymentmethod(booking.getPaymentmethod());
        response.setStatus(booking.getStatus());
        response.setCarIdcar(booking.getCarIdcar());
        response.setCarIdcarowner(booking.getCarIdcarowner());
        response.setUserIduser(booking.getUserIduser());
        return response;
    }

        private String getPaymentInstruction(Booking booking) {
        String paymentInstruction = "";
        if ("my wallet".equals(booking.getPaymentmethod())) {
            Optional<User> userOptional = userRepository.findById(booking.getUserIduser());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                paymentInstruction = "Current wallet balance: " + user.getWallet() + ". Please make sure to have sufficient balance when you return the car. [Link to Wallet]";
            }
        } else if ("Cash".equals(booking.getPaymentmethod()) || "bank transfer".equals(booking.getPaymentmethod())) {
            if ("Pending deposit".equals(booking.getStatus()) || "Pending payment".equals(booking.getStatus())) {
                paymentInstruction = "Our operator will contact you for further instruction.";
            } else {
                paymentInstruction = "Payment fulfilled.";
            }
        }
        return paymentInstruction;
    }
}