package datn.datnbe.Controller;

import datn.datnbe.Service.ViewHomePageasCustomerService;
import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.dto.response.AuthenResponse;
import datn.datnbe.dto.response.FeedbackResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/viewhomeCustomer")
public class ViewHomePageasCustomerController {

    @Autowired
    ViewHomePageasCustomerService viewHomePageasCustomerService;

    @GetMapping("/feedback")
    public ApiResponse<List<FeedbackResponse>> getMyInfo() {
        return  viewHomePageasCustomerService.getFeedbackRes();
    }

    @GetMapping("/auth")
    public ApiResponse<AuthenResponse> authRole(){
        return ApiResponse.<AuthenResponse>builder()
                .result(viewHomePageasCustomerService.getUserRole())
                .build();
    }

    @GetMapping("/top-cities")
    public ApiResponse<List<Object[]>> getTop5CitiesWithMostCars() {
        return viewHomePageasCustomerService.getTop5CitiesWithMostCars();
    }
}
