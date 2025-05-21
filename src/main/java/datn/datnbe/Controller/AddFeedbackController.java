package datn.datnbe.Controller;

import datn.datnbe.Entity.Feedback;
import datn.datnbe.Service.AddFeedbackService;
import datn.datnbe.dto.request.AddReportRequest;
import datn.datnbe.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/add_feedback")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AddFeedbackController {
    @Autowired
    AddFeedbackService addFeedbackService;
    @PostMapping("/{idBooking}")
    public ApiResponse addFeedback(@PathVariable("idBooking") int idBooking, @RequestBody AddReportRequest feedback){
        return addFeedbackService.creatFeedback(idBooking, feedback);
    }
}
