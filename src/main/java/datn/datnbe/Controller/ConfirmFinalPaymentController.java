package datn.datnbe.Controller;

import datn.datnbe.Service.ConfirmFinalPaymentService;
import datn.datnbe.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ConfirmFinalPaymentController {
    ConfirmFinalPaymentService confirmFinalPaymentService;

    @PostMapping("/confirmfinalpayment/{idbooking}")
    public ApiResponse confirmFinalPayment(@PathVariable("idbooking") Integer idbooking){
        return ApiResponse
                .builder()
                .result(confirmFinalPaymentService.confirmFinalPayment(idbooking))
                .build();
    }

}
