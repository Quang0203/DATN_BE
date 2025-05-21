package datn.datnbe.Service;

import datn.datnbe.Entity.Car;
import datn.datnbe.Entity.User;
import datn.datnbe.Exception.AppException;
import datn.datnbe.Exception.ErrorCode;
import datn.datnbe.Mapper.CarMapper;
import datn.datnbe.Repository.AdditionalfunctionsRepository;
import datn.datnbe.Repository.CarRepository;
import datn.datnbe.Repository.TermofuseRepository;
import datn.datnbe.Repository.UserRepository;
import datn.datnbe.dto.request.AddCarRequest;
import datn.datnbe.dto.request.EditCarRequest;
import datn.datnbe.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Builder
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EditCarService {
    @Autowired
    CarRepository carRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TermofuseRepository termofuseRepository;

    @Autowired
    AdditionalfunctionsRepository additionalfunctionsRepository;

    public ApiResponse editCar(Integer idCar, EditCarRequest request) {
        var oldcar = carRepository.findById(idCar).orElseThrow(()
                -> new AppException(ErrorCode.USER_NOTFOUND));
        if (Objects.equals(oldcar.getStatus(), "Booked")){
            throw new AppException(ErrorCode.CANNOT_EDIT);
        }
        Car car = new Car(idCar, request.getName(), request.getBrand(), request.getModel(), request.getColor(),
                request.getNumberofseats(), request.getProductionyears(), request.getTranmissiontype(), request.getFueltype(),
                request.getMileage(), request.getFuelconsumption(), request.getBaseprice(), request.getDeposite(),
                request.getAddress(), request.getDescripton(), request.getImages(), request.getStatus(), request.getIdcarowner());
        car.setStatus("Available");
        var context = SecurityContextHolder.getContext();
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
}
