package datn.datnbe.Service.chatbot;

import datn.datnbe.Entity.Car;
import datn.datnbe.Repository.CarRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatbotService {

    private final GrokQaService grokQaService;
    private final CarRepository carRepository;

    public ChatbotService(GrokQaService grokQaService, CarRepository carRepository) {
        this.grokQaService = grokQaService;
        this.carRepository = carRepository;
    }

    public String handleChat(String question) {
        // 1. Phân tích query (giữ nguyên) ...
        List<String> carFields = Arrays.asList("brand", "model", "numberofseats", "baseprice", "descripton", "status");

        Map<String, Object> criteria = grokQaService.analyzeQuery(question, carFields);

        // 2. Lấy toàn bộ danh sách xe (hoặc filter theo criteria nếu muốn)
        List<Car> cars = carRepository.findAll();

        // 3. Xây dựng carData (chỉ để relative url)
        List<Map<String, Object>> carData = cars.stream().map(car -> {
            Map<String, Object> map = new HashMap<>();
            map.put("brand", car.getBrand());
            map.put("model", car.getModel());
            map.put("numberofseats", car.getNumberofseats());
            map.put("baseprice", car.getBaseprice());
            map.put("address", car.getAddress());
            map.put("descripton", car.getDescripton());
            map.put("status", car.getStatus());
            // Chỉ để relative path
            map.put("url", "/view-car/" + car.getIdcar());
            return map;
        }).collect(Collectors.toList());

        // 4. Tạo prompt full, yêu cầu x.ai trả markdown link
        String prompt = "Dựa vào dữ liệu xe thuê dưới đây và câu hỏi của khách hàng, " +
                "hãy tư vấn lựa chọn xe phù hợp và khi liệt kê mỗi xe, hãy đưa link theo dạng markdown. " +
                "Cú pháp markdown: `[Tên xe](http://localhost:4200/view-car/{id})`. Ví dụ: `[Xem Toyota Camry](http://localhost:4200/view-car/123)`.\n" +
                "Câu hỏi: " + question + "\n" +
                "Dữ liệu xe (mỗi item gồm brand, model, numberofseats, baseprice, address,  descripton, status, url): " + carData.toString() + "\n" +
                "Hãy trả về câu trả lời chi tiết, không quá dài, kèm markdown link cho từng gợi ý xe.";

        // 5. Gọi x.ai
        String answer = grokQaService.assembleAnswer(prompt, carData);

        return answer;
    }

    public String handleChatWithHistory(List<Map<String, String>> messages) {
        // 1. Build prompt từ history
        StringBuilder promptBuilder = new StringBuilder();
        for (Map<String, String> msg : messages) {
            String role = msg.get("role");
            String content = msg.get("content");
            promptBuilder.append(role.equals("user") ? "Người dùng: " : "Bot: ").append(content).append("\n");
        }
        String prompt = promptBuilder.toString();

        // 2. Lấy danh sách xe
        List<Car> cars = carRepository.findAll();

        // 3. Xây dựng carData
        List<Map<String, Object>> carData = cars.stream().map(car -> {
            Map<String, Object> map = new HashMap<>();
            map.put("brand", car.getBrand());
            map.put("model", car.getModel());
            map.put("numberofseats", car.getNumberofseats());
            map.put("baseprice", car.getBaseprice());
            map.put("address", car.getAddress());
            map.put("descripton", car.getDescripton());
            map.put("status", car.getStatus());
            map.put("url", "/view-car/" + car.getIdcar());
            return map;
        }).collect(Collectors.toList());

        // 4. Prompt kèm yêu cầu markdown
        String fullPrompt = "Dựa vào dữ liệu xe thuê dưới đây và cuộc trò chuyện trước đó, " +
                "hãy tư vấn lựa chọn xe phù hợp ưu tiên tiêu chí address. Khi liệt kê mỗi xe, hãy dùng markdown link theo cú pháp `[Tên xe](http://localhost:4200/view-car/{id})`. " +
                "Ví dụ: `[Xem Toyota Camry](http://localhost:4200/view-car/123)`.\n" +
                "Cuộc trò chuyện: " + prompt + "\n" +
                "Dữ liệu xe (brand, model, numberofseats, baseprice, address, descripton, status, url): " + carData.toString() + "\n" +
                "Hãy trả về câu trả lời kèm markdown link cho từng xe được đề xuất.";

        // 5. Gọi x.ai
        String answer = grokQaService.assembleAnswer(fullPrompt, carData);
        return answer;
    }
}