package datn.datnbe.Service;

import datn.datnbe.Entity.Additionalfunctions;
import datn.datnbe.Entity.Car;
import datn.datnbe.Entity.Termofuse;
import datn.datnbe.Repository.AdditionalfunctionsRepository;
import datn.datnbe.Repository.CarRepository;
import datn.datnbe.Repository.TermofuseRepository;
import datn.datnbe.dto.response.ViewCarDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ViewCarDetailsService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private TermofuseRepository termofuseRepository;

    @Autowired
    private AdditionalfunctionsRepository additionalfunctionsRepository;

    public ViewCarDetailsResponse getCarDetails(int id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found"));
        Termofuse termsOfUse = (Termofuse) termofuseRepository.findByIdcar(id);
        Additionalfunctions additionalFunctions = (Additionalfunctions) additionalfunctionsRepository.findByIdcar(id);

        return ViewCarDetailsResponse.builder()
                .car(car)
                .termsOfUse(termsOfUse)
                .additionalFunctions(additionalFunctions)
                .build();
    }

}
