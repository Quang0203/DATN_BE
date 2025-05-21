package datn.datnbe.Controller;


import datn.datnbe.Entity.User;
import datn.datnbe.Service.UserService;
import datn.datnbe.Service.ViewHomePageasCustomerService;
import datn.datnbe.dto.request.UpdatePasswordRequest;
import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.dto.response.AuthenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/editProfile")
public class EditProfileController {
    @Autowired
    private UserService userService;

    @PutMapping("/userinfo/{id}")
    public ApiResponse<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        ApiResponse<User> apiReponse = new ApiResponse<>();
        apiReponse.setResult(userService.updateProfile(id, user));
        return apiReponse;
    }

    @GetMapping("/getId")
    public ApiResponse authRole(){
        int id = userService.getMyInfo().getIduser();
        return ApiResponse.builder()
                .result(id)
                .build();
    }

    @PutMapping("/userpass/{id}")
    public ApiResponse<User> updatePass(@PathVariable Integer id, @RequestBody UpdatePasswordRequest upr) {
        ApiResponse<User> apiReponse = new ApiResponse<>();
        apiReponse.setResult(userService.updateProfilePassword(id, upr.getPassword()));
        return apiReponse;
    }
}
