package datn.datnbe.Service;

import datn.datnbe.Entity.Booking;
import datn.datnbe.Entity.Feedback;
import datn.datnbe.Exception.AppException;
import datn.datnbe.Exception.ErrorCode;
import datn.datnbe.Repository.BookingRepository;
import datn.datnbe.Repository.FeedbackRepository;
import datn.datnbe.Repository.UserRepository;
import datn.datnbe.dto.request.AddReportRequest;
import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.dto.response.UserRespone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AddFeedbackService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    FeedbackRepository feedbackRepository;

    public ApiResponse<Feedback> creatFeedback(int bookingId, AddReportRequest report){
        Feedback feedback = new Feedback();
        feedback.setContent(report.getContent());
        feedback.setRate(report.getRate());
        feedback.setDatetime(LocalDateTime.now());
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOTFOUND));
        feedback.setBookingCarIdcar(booking.getCarIdcar());
        feedback.setBookingCarIdcarowner(booking.getCarIdcarowner());
        feedback.setBookingIdbooking(booking.getIdbooking());
        feedback.setBookingUserIduser(booking.getUserIduser());
        feedbackRepository.save(feedback);
        return ApiResponse.<Feedback>builder()
                .result(feedback)
                .build();
    }
}
