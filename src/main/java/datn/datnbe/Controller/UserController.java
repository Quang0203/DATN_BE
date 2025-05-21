package datn.datnbe.Controller;


import datn.datnbe.Service.UserService;
import datn.datnbe.dto.request.ForgotPasswordEmailRequest;
import datn.datnbe.dto.request.ForgotPasswordRequest;
import datn.datnbe.dto.request.ResetPasswordRequest;
import datn.datnbe.dto.request.UserCreationRequest;
import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.dto.response.UserRespone;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping()
    public ApiResponse getUser() {
        return userService.getUsers();
    }

    @GetMapping("{userId}")
    public ApiResponse<UserRespone> getUserById(@PathVariable("userId") Integer userId) {
        return ApiResponse.<UserRespone>builder()
                .result(userService.getUserById(userId))
                .build();
    }

    @GetMapping("/myInfo")
    public ApiResponse<UserRespone> getMyInfo() {
        return ApiResponse.<UserRespone>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PostMapping("/create")
    @CrossOrigin(origins = "http://localhost:4200")
    public ApiResponse createUser(@RequestBody @Valid UserCreationRequest request) {
        return userService.createUser(request);
    }


    @PutMapping("/{userId}/update")
    public ApiResponse updateUser(@PathVariable("userId") Integer userId, @RequestBody @Valid UserCreationRequest request) {
        return userService.updateUser(userId, request);
    }

//    @PutMapping("/forgot")
//    public ApiResponse forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
//        return userService.fotgotPassword(request);
//    }

    @PostMapping("/forgot")
    public ApiResponse forgot(@RequestBody @Valid ForgotPasswordEmailRequest req) {
        userService.forgotPassword(req.getEmail());
        return ApiResponse.builder().message("Email sent").build();
    }
    @PostMapping("/reset-password")
    public ApiResponse reset(@RequestBody @Valid ResetPasswordRequest req) {
        userService.resetPassword(req.getToken(), req.getNewpassword(), req.getConfirmpassword());
        return ApiResponse.builder().message("Password reset successful").build();
    }

    @DeleteMapping("/{userId}/delete")
    public ApiResponse deleteUser(@PathVariable("userId") Integer userId) {
        return userService.deleteUser(userId);
    }

    @DeleteMapping("deleteAll")
    public ApiResponse deleteAllUser() {
        return userService.deleteAllUser();
    }
}