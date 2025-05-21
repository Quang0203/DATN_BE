package datn.datnbe.Service;

import datn.datnbe.Entity.Feedback;
import datn.datnbe.Entity.User;
import datn.datnbe.Exception.AppException;
import datn.datnbe.Exception.ErrorCode;
import datn.datnbe.Mapper.FeedbackMapper;
import datn.datnbe.Repository.BookingRepository;
import datn.datnbe.Repository.CarRepository;
import datn.datnbe.Repository.FeedbackRepository;
import datn.datnbe.Repository.UserRepository;
import datn.datnbe.dto.response.FeedbackResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ViewFeedbackReportService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private CarRepository carRepository; // New field

    @Autowired
    private BookingRepository bookingRepository; // New field

//    public List<FeedbackResponse> getFeedbackReport() {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
//
//        if (!"CAROWNER".equals(user.getRole())) {
//            throw new AppException(ErrorCode.UNAUTHORIZED);
//        }
//
//        List<Feedback> feedbackList = feedbackRepository.findAllByBookingCarIdcarowner(user.getIduser());
//        return feedbackList.stream()
//                .map(feedbackMapper::toFeedbackResponse)
//                .collect(Collectors.toList());
//    }

    public List<FeedbackResponse> getFeedbackReport(Integer rate) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        if (!"CAROWNER".equals(user.getRole())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        List<Feedback> feedbackList;
        if (rate == null) {
            feedbackList = feedbackRepository.findAllByBookingCarIdcarowner(user.getIduser());
        } else {
            feedbackList = feedbackRepository.findAllByBookingCarIdcarownerAndRate(user.getIduser(), rate);
        }

        return feedbackList.stream()
                .map(feedback -> {
                    var response = feedbackMapper.toFeedbackResponse(feedback);
                    var car = carRepository.findById(feedback.getBookingCarIdcar()).orElseThrow(() -> new AppException(ErrorCode.CAR_NOTFOUND));
                    var booking = bookingRepository.findById(feedback.getBookingIdbooking()).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOTFOUND));
                    var userres = userRepository.findById(feedback.getBookingUserIduser()).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
                    response.setCarName(car.getName());
                    response.setCarModel(car.getModel());
                    response.setCarImage(car.getImages());
                    response.setUserName(userres.getName());
                    response.setBookingStartDate(booking.getStartdatetime().toString());
                    response.setBookingEndDate(booking.getEnddatetime().toString());
                    return response;
                })
                .sorted((a, b) -> b.getDatetime().compareTo(a.getDatetime())) // Sort from newest to oldest
                .collect(Collectors.toList());
    }

    public double getAverageRating(Integer carOwnerId) {
        List<Feedback> feedbackList = feedbackRepository.findAllByBookingCarIdcarowner(carOwnerId);
        double averageRating = feedbackList.stream()
                .mapToDouble(Feedback::getRate)
                .average()
                .orElse(0.0);
        return Math.round(averageRating * 100.0) / 100.0; // Round to 2 decimal places
    }

    public double getAverageRatingByIdCar(Integer carId) {
        List<Feedback> feedbackList = feedbackRepository.findAllByBookingCarIdcar(carId);
        double averageRating = feedbackList.stream()
                .mapToDouble(Feedback::getRate)
                .average()
                .orElse(0.0);
        return Math.round(averageRating * 100.0) / 100.0;
    }


}

