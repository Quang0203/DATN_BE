package datn.datnbe.Controller;


import datn.datnbe.Entity.Additionalfunctions;
import datn.datnbe.Entity.Termofuse;
import datn.datnbe.Service.AddCarService;
import datn.datnbe.dto.request.AddCarRequest;
import datn.datnbe.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/car")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AddCarController {

    @Autowired
    AddCarService addCarService;

    @PostMapping("/addcar")
    @CrossOrigin(origins = "http://localhost:4200")
    public ApiResponse addCar(@RequestBody AddCarRequest request)
    {
        return addCarService.addCar(request);
    }

    @PostMapping("/addterm")
    public ApiResponse addTermofuse(@RequestBody Termofuse termofuse){return addCarService.addTerm(termofuse);}

    @PostMapping("/addfunction")
    public ApiResponse addFunction(@RequestBody Additionalfunctions additionalfunctions){return addCarService.addFunctions(additionalfunctions);}
}
