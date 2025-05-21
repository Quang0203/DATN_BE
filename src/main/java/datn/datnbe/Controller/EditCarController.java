package datn.datnbe.Controller;

import datn.datnbe.Entity.Additionalfunctions;
import datn.datnbe.Entity.Termofuse;
import datn.datnbe.Service.AddCarService;
import datn.datnbe.Service.EditCarService;
import datn.datnbe.dto.request.AddCarRequest;
import datn.datnbe.dto.request.EditCarRequest;
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
public class EditCarController {

    @Autowired
    EditCarService editCarService;

    @Autowired
    AddCarService addCarService;

    @PostMapping("/editcar/{idcar}")
    @CrossOrigin(origins = "http://localhost:4200")
    public ApiResponse addCar(@PathVariable Integer idcar ,@RequestBody EditCarRequest request) {
        return editCarService.editCar(idcar, request);
    }

    @PostMapping("/editterm")
    public ApiResponse addTermofuse(@RequestBody Termofuse termofuse){return addCarService.addTerm(termofuse);}

    @PostMapping("/editfunction")
    public ApiResponse addFunction(@RequestBody Additionalfunctions additionalfunctions){return addCarService.addFunctions(additionalfunctions);}
}
