package datn.datnbe.Controller;


import datn.datnbe.Service.SearchCarService;
import datn.datnbe.dto.request.SearchCarRequest;
import datn.datnbe.dto.request.SearchCarRequestNew;
import datn.datnbe.dto.response.ApiResponse;
import datn.datnbe.dto.response.SearchCarResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/searchCar")
public class SearchCarController {

    @Autowired
    SearchCarService searchCarService;


    @PostMapping
    public ApiResponse<List<SearchCarResponse>> searchCar(@RequestBody SearchCarRequest searchCarRequest){
        return searchCarService.getListCar(searchCarRequest);
    }

    @PostMapping("/new")
    public ApiResponse<List<SearchCarResponse>> searchCarNew(@RequestBody SearchCarRequestNew searchCarRequest){
        return searchCarService.findAvailableCars(searchCarRequest);
    }

}
