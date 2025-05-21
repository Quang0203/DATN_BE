package datn.datnbe.Controller;


import datn.datnbe.Entity.User;
import datn.datnbe.Exception.AppException;
import datn.datnbe.Exception.ErrorCode;
import datn.datnbe.Repository.UserRepository;
import datn.datnbe.Service.ViewFeedbackReportService;
import datn.datnbe.dto.request.ViewFeedbackReportRequest;
import datn.datnbe.dto.response.FeedbackResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/viewFeedbackReport")
public class ViewFeedbackReportController {

    @Autowired
    private ViewFeedbackReportService viewFeedbackReportService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<FeedbackResponse> getFeedbackReport(@RequestParam(required = false) Integer rate) {
        return viewFeedbackReportService.getFeedbackReport(rate);
    }

    @GetMapping("/averageRating")
    public double getAverageRating() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        if (!"CAROWNER".equals(user.getRole())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return viewFeedbackReportService.getAverageRating(user.getIduser());
    }

    @GetMapping("/cars/{idcar}/averageRatingByIdCar")
    public ResponseEntity<Double> getAverageRating(@PathVariable Integer idcar) {
        double averageRating = viewFeedbackReportService.getAverageRatingByIdCar(idcar);
        return ResponseEntity.ok(averageRating);
    }

}
