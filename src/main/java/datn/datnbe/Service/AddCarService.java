package datn.datnbe.Service;


import datn.datnbe.Entity.Additionalfunctions;
import datn.datnbe.Entity.Car;
import datn.datnbe.Entity.Termofuse;
import datn.datnbe.Entity.User;
import datn.datnbe.Exception.AppException;
import datn.datnbe.Exception.ErrorCode;
import datn.datnbe.Mapper.CarMapper;
import datn.datnbe.Repository.AdditionalfunctionsRepository;
import datn.datnbe.Repository.CarRepository;
import datn.datnbe.Repository.TermofuseRepository;
import datn.datnbe.Repository.UserRepository;
import datn.datnbe.dto.request.AddCarRequest;
import datn.datnbe.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Builder
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddCarService {

    @Autowired
    CarRepository carRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CarMapper carMapper;

    @Autowired
    TermofuseRepository termofuseRepository;

    @Autowired
    AdditionalfunctionsRepository additionalfunctionsRepository;

    public ApiResponse addCar(AddCarRequest request)
    {
        Car car = carMapper.toCar(request);
        car.setStatus("Available");
        var context = SecurityContextHolder.getContext();
//        Jwt jwt = (Jwt) context.getAuthentication().getPrincipal();
//        var claims = jwt.getClaims();
//        Long longIdUser = (Long) claims.get("id");
//        int idUser = longIdUser.intValue();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        car.setIdcarowner(user.getIduser());
        carRepository.save(car);
        ApiResponse apiResponse = new ApiResponse()
                .builder()
                .result(car)
                .build();
        return apiResponse;
    }

    public ApiResponse addTerm(Termofuse termofuse){
        termofuseRepository.save(termofuse);
        ApiResponse apiResponse = new ApiResponse()
                .builder()
                .result(termofuse)
                .build();
        return apiResponse;
    }

    public ApiResponse addFunctions(Additionalfunctions additionalfunctions){
        additionalfunctionsRepository.save(additionalfunctions);
        ApiResponse apiResponse = new ApiResponse()
                .builder()
                .result(additionalfunctions)
                .build();
        return apiResponse;
    }

}
