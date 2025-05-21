package datn.datnbe.Service;

import datn.datnbe.Entity.Feedback;
import datn.datnbe.Entity.User;
import datn.datnbe.Exception.AppException;
import datn.datnbe.Exception.ErrorCode;
import datn.datnbe.Handler.Sorted.FeedbackSorter;
import datn.datnbe.Mapper.FeedbackMapper;
import datn.datnbe.Repository.CarRepository;
import datn.datnbe.Repository.FeedbackRepository;
import datn.datnbe.Repository.UserRepository;
import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.dto.response.AuthenResponse;
import datn.datnbe.dto.response.FeedbackResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ViewHomePageasCustomerService {
    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FeedbackMapper feedbackMapper;

    @Autowired
    CarRepository carRepository;

    public List<Feedback> getTopFeedBack(){
        List<Feedback> allFeedback = feedbackRepository.findAll();
        List<Feedback> sortedFeedback = FeedbackSorter.sortFeedback(allFeedback);
        return sortedFeedback.size() <= 5 ? sortedFeedback : sortedFeedback.subList(0, 4);
    }

    public ApiResponse<List<FeedbackResponse>> getFeedbackRes(){
        List<Feedback> feedbackList = getTopFeedBack();
        List<FeedbackResponse> feedbackResponseList = new ArrayList<>();
        for (Feedback i : feedbackList){
            User user = userRepository.findById(i.getBookingUserIduser()).orElse(null);
            FeedbackResponse feedbackResponse = feedbackMapper.toFeedbackResponse(i);
            feedbackResponse.setUser(user);
            feedbackResponseList.add(feedbackResponse);
        }
        ApiResponse<List<FeedbackResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(feedbackResponseList);
        return apiResponse;
    }

    public ApiResponse<List<Feedback>> getFeedBack(){
        List<Feedback> feedbackList = getTopFeedBack();
        ApiResponse<List<Feedback>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(feedbackList);
        return apiResponse;
    }

//    @PostAuthorize("returnObject.email == authentication.name")
    public AuthenResponse getUserRole(){
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        return new AuthenResponse(user.getRole());
    }
    public ApiResponse<List<Object[]>> getTop5CitiesWithMostCars() {
        ApiResponse<List<Object[]>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(carRepository.findTop5CitiesWithMostCars());
        return apiResponse;
    }

}
