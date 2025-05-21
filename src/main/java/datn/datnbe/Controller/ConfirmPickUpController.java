package datn.datnbe.Controller;

import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.Service.ConfirmPickUpService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/confirmpickup")
public class ConfirmPickUpController {

    @Autowired
    ConfirmPickUpService confirmPickUpService;

    @PostMapping("/{idbooking}")
    public ApiResponse confirmPickUpService(@PathVariable("idbooking") Integer idbooking){
        return new ApiResponse()
                .builder()
                .result(confirmPickUpService.confirmPickUpService(idbooking))
                .build();
    }
}
