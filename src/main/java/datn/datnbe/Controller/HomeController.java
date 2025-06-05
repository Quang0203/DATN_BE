package datn.datnbe.Controller;

import datn.datnbe.Repository.CarRepository;
import datn.datnbe.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/viewHomepage")
public class HomeController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CarRepository carRepository;

    @GetMapping("/getCity")
    public ApiResponse getCity() {
        ApiResponse<List<Object[]>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(carRepository.findTop5CitiesWithMostCars());
        return apiResponse;
    }

    @GetMapping("/getFeedback")
    public ApiResponse getFeedback(){

        String sql2 = "SELECT u.name AS `UserName`, f.content AS `FeedbackContent`, f.rate AS `Rating`, f.dateTime AS `Date` " +
                "FROM feedback f " +
                "JOIN booking b ON b.idBooking = f.Booking_idBooking " +
                "JOIN user u ON u.idUser = b.User_idUser " +
                "WHERE f.rate = 5 " +
                "AND f.content IS NOT NULL " +
                "ORDER BY f.dateTime DESC " +
                "LIMIT 4;";
        List<Map<String, Object>> results2 = jdbcTemplate.queryForList(sql2);
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(results2);
        return apiResponse;
    }
}
