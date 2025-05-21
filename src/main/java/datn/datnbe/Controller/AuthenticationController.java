package datn.datnbe.Controller;


import datn.datnbe.Service.AuthenticationService;
import datn.datnbe.dto.request.AuthenticationRequest;
import datn.datnbe.dto.request.IntrospectRequest;
import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.dto.response.AuthenticationRespone;
import datn.datnbe.dto.response.IntrospectRespone;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationController {
    AuthenticationService authenticationService;
    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:4200")
    public ApiResponse<AuthenticationRespone> authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        var result = authenticationService.authenticate(authenticationRequest);
        ApiResponse apiReponse = new ApiResponse();
        apiReponse.setResult(result);
        System.out.println(authenticationRequest.getEmail());
        System.out.println(authenticationRequest.getPassword());
        System.out.println("controller sucess");
        return apiReponse;
    }
    @PostMapping("/introspect")
    @CrossOrigin(origins = "http://localhost:4200")
    public ApiResponse<IntrospectRespone> instrospect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        ApiResponse apiReponse = new ApiResponse();
        apiReponse.setResult(result);
        return apiReponse;
    }
}