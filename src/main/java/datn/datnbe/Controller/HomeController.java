package datn.datnbe.Controller;

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

    @GetMapping("/getCity")
    public ApiResponse getCity() {
        String sql = "SELECT address, COUNT(*) AS car_count, MIN(images) AS image " +
                "FROM car " +
                "GROUP BY address " +
                "ORDER BY car_count DESC " +
                "LIMIT 6";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> result : results) {
            Object carCountObj = result.get("car_count");
            if (carCountObj instanceof Number) {
                Number carCountNumber = (Number) carCountObj;
                int carCount = carCountNumber.intValue();
                int roundedCount = (carCount / 10) * 10;
                result.put("car_count_rounded", roundedCount + "+");
            } else {
                result.put("car_count_rounded", "Error");
            }
        }
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(results);
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
