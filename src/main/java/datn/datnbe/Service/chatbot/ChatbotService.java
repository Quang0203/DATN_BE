package datn.datnbe.Service.chatbot;

import datn.datnbe.Entity.Car;
import datn.datnbe.Repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatbotService {

    private final GrokQaService grokQaService;
    private final CarRepository carRepository;

    public ChatbotService(GrokQaService grokQaService, CarRepository carRepository) {
        this.grokQaService = grokQaService;
        this.carRepository = carRepository;
    }

    public String handleChat(String question) {
        // Bước 1: Gửi câu hỏi và danh sách field liên quan đến xe thuê cho API x.ai để phân tích tiêu chí tìm kiếm.
        // Các field được sử dụng: brand, model, numberofseats, baseprice, descripton, status.
        List<String> carFields = Arrays.asList("brand", "model", "numberofseats", "baseprice", "descripton", "status");
        // Ví dụ API trả về: {"brand": "Toyota", "model": "Camry", "numberofseats": 4, "baseprice": "500", "status": "available"}
        Map<String, Object> criteria = grokQaService.analyzeQuery(question, carFields);

        // Bước 2: Dựa vào tiêu chí (criteria) có thể áp dụng lọc xe thuê từ DB.
        // Ví dụ: Nếu tiêu chí có "brand" hoặc "model" cụ thể, bạn có thể xây dựng thêm phương thức lọc.
        // Ở đây, ví dụ đơn giản là lấy toàn bộ xe từ DB.
        List<Car> cars = carRepository.findAll();

        // Chuyển đổi danh sách xe thành danh sách Map để gửi cho API x.ai
        List<Map<String, Object>> carData = cars.stream().map(car -> {
            Map<String, Object> map = new HashMap<>();
            map.put("brand", car.getBrand());
            map.put("model", car.getModel());
            map.put("numberofseats", car.getNumberofseats());
            map.put("baseprice", car.getBaseprice());
            map.put("descripton", car.getDescripton());
            map.put("status", car.getStatus());
            map.put("url", "/localhost:4200/view-car/" + car.getIdcar());
            return map;
        }).collect(Collectors.toList());

        // Bước 3: Gửi câu hỏi và dữ liệu xe cho API x.ai để tổng hợp câu trả lời tư vấn cuối cùng.
        return grokQaService.assembleAnswer(question, carData);
    }
}
