package datn.datnbe.Service;


import datn.datnbe.Config.JwtTokenProvider;
import datn.datnbe.Entity.User;
import datn.datnbe.Exception.AppException;
import datn.datnbe.Exception.ErrorCode;
import datn.datnbe.Mapper.UserMapper;
import datn.datnbe.Repository.UserRepository;
import datn.datnbe.dto.request.ForgotPasswordRequest;
import datn.datnbe.dto.request.UserCreationRequest;
import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.dto.response.UserRespone;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.app.frontend.url}") private String frontendUrl;

    private final StringRedisTemplate redisTemplate;

    private final JavaMailSender mailSender;

    // TTL cho token (giây)
    private static final long RESET_TOKEN_TTL = 3600L;

    public UserRespone getUserInfoFromToken(String token) {
        String email = jwtTokenProvider.getUsernameFromToken(token);
        return userMapper.toUserRespone(userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse getUsers() {
        ApiResponse apiReponse = new ApiResponse();
        apiReponse.setResult(userRepository.findAll());
        apiReponse.setMessage("Sucess");
        return apiReponse;
    }

    public UserRespone getUserById(Integer userId) {
        return userMapper.toUserRespone(userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND)));
    }

    @PostAuthorize("returnObject.email == authentication.name")
    public UserRespone getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        return userMapper.toUserRespone(userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND)));
    }

    public ApiResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        log.info("Driving license: {}", user.getDrivinglicense());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        userRepository.save(user);
        ApiResponse apiReponse = new ApiResponse();
        apiReponse.setResult(user);
        return apiReponse;
    }

    public ApiResponse updateUser(Integer userId, UserCreationRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        ApiResponse apiResponse = new ApiResponse();

        // Nếu mật khẩu trong yêu cầu khác với mật khẩu hiện tại, mã hóa lại mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Cập nhật các trường khác
        user = userMapper.updateUser(user, request);
        userRepository.save(user);
        apiResponse.setResult(user);
        return apiResponse;
    }

//    public ApiResponse fotgotPassword(ForgotPasswordRequest request) {
//        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
//        if (request.getNewpassword().equals(request.getConfirmpassword())) {
//            user.setPassword(passwordEncoder.encode(request.getNewpassword()));
//        } else throw new AppException(ErrorCode.PASSWORC_NOTEQUAL);
//        System.out.println(request.getNewpassword().equals(request.getConfirmpassword()));
//        System.out.println(request.getNewpassword());
//        System.out.println(request.getConfirmpassword());
//
//        userRepository.save(user);
//        ApiResponse apiReponse = new ApiResponse()
//                .builder()
//                .result(user)
//                .build();
//        return apiReponse;
//    }

    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        // tạo token (UUID)
        String token = UUID.randomUUID().toString();

        // lưu vào Redis: key = "pwd-reset:"+token, value = email, TTL = 1h
        String key = "pwd-reset:" + token;
        redisTemplate.opsForValue().set(key, email, RESET_TOKEN_TTL, TimeUnit.SECONDS);

        // gửi mail
        String link = frontendUrl + "/reset-password?token=" + token;
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Reset your password");
        mail.setText("Click here to reset your password:\n" + link);
        mailSender.send(mail);
    }

    public void resetPassword(String token, String newPwd, String confirmPwd) {
        if (!newPwd.equals(confirmPwd)) {
            throw new AppException(ErrorCode.PASSWORC_NOTEQUAL);
        }

        String key = "pwd-reset:" + token;
        // lấy email từ Redis
        String email = redisTemplate.opsForValue().get(key);
        if (email == null) {
            // token không tồn tại hoặc đã hết hạn
            throw new AppException(ErrorCode.INVALID_OR_EXPIRED_RESETPASSWORD_TOKEN);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        user.setPassword(passwordEncoder.encode(newPwd));
        userRepository.save(user);

        // xoá token sau khi dùng
        redisTemplate.delete(key);
    }


    public ApiResponse deleteUser(Integer userId) {
        userRepository.deleteById(userId);
        ApiResponse apiReponse = new ApiResponse();
        apiReponse.setMessage("Xóa thành công");
        return apiReponse;
    }

    public ApiResponse deleteAllUser() {
        userRepository.deleteAll();
        ApiResponse apiReponse = new ApiResponse();
        apiReponse.setMessage("Xóa thành công");
        return apiReponse;
    }


    //Quang's source
//    public User updateProfile(Long id, User updatedUser) {
//        User user = userRepository.findById(Math.toIntExact(id)).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
//        user.setName(updatedUser.getName());
//        user.setDateofbirth(updatedUser.getDateofbirth());
//        user.setEmail(updatedUser.getEmail());
//        user.setPhoneno(updatedUser.getPhoneno());
//        user.setNationalidno(updatedUser.getNationalidno());
//        user.setAddress(updatedUser.getAddress());
//        user.setDrivinglicense(updatedUser.getDrivinglicense());
//        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
//        return userRepository.save(user);
//    }

    public User updateProfile(Integer id, User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            updatedUser.setIduser(id);
//            updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            return userRepository.save(updatedUser);
        } else {
            throw new AppException(ErrorCode.USER_NOTFOUND);
        }
    }

    public User updateProfilePassword(Integer id, String password) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setIduser(id);
            existingUser.setPassword(passwordEncoder.encode(password));
            System.out.println(passwordEncoder.encode(password) + "    " + password);
            return userRepository.save(existingUser);
        } else {
            throw new AppException(ErrorCode.USER_NOTFOUND);
        }
    }

}