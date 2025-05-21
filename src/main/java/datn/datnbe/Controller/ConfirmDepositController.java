package datn.datnbe.Controller;

import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.Service.ConfirmDepositService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ConfirmDepositController {
    ConfirmDepositService confirmDepositService;

    @PostMapping("/confirmdeposit/{idbooking}")
    public ApiResponse confirmdeposit(@PathVariable("idbooking") Integer idbooking){
        return ApiResponse
                .builder()
                .result(confirmDepositService.confirmDeposit(idbooking))
                .build();
    }
}
