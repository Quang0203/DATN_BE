package datn.datnbe.Service.chatbot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class GrokQaService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // URL cơ bản của API x.ai, ví dụ: https://api.x.ai/v1
    @Value("${xai.api.url}")
    private String xaiApiUrl;

    @Value("${xai.api.key}")
    private String xaiApiKey;

    public GrokQaService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Phân tích câu hỏi liên quan đến thuê xe và danh sách các field từ entity Car.
     * Yêu cầu API x.ai trả về một JSON object chứa các tiêu chí tìm kiếm phù hợp.
     *
     * Ví dụ prompt gửi đi:
     * "Phân tích câu hỏi liên quan đến thuê xe: 'Tôi cần thuê một xe cho gia đình 5 người, giá thuê phải hợp lý'
     * Danh sách field: [brand, model, numberofseats, baseprice, descripton, status]
     * Trả về JSON tiêu chí, ví dụ: {\"brand\": \"Toyota\", \"model\": \"Innova\", \"numberofseats\": 5, \"baseprice\": \"600\", \"status\": \"available\"}"
     */
    public Map<String, Object> analyzeQuery(String question, List<String> carFields) {
        String prompt = "Phân tích câu hỏi liên quan đến thuê xe: \"" + question + "\"\n"
                + "Danh sách các field liên quan: " + carFields.toString() + "\n"
                + "Trả về một JSON object chứa các tiêu chí tìm kiếm phù hợp. Ví dụ: {\"brand\": \"Toyota\", \"model\": \"Innova\", \"numberofseats\": 5, \"baseprice\": \"600\", \"status\": \"available\"}";

        Map<String, Object> payload = new HashMap<>();
//        payload.put("model", "grok-2-latest");
        payload.put("model", "grok-3-mini-beta");
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        payload.put("messages", messages);
        payload.put("temperature", 0.0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + xaiApiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        log.info("URL đến API x.ai: {}", xaiApiUrl + "/chat/completions");
        ResponseEntity<Map> response = restTemplate.postForEntity(xaiApiUrl + "/chat/completions", request, Map.class);
        log.info("Phản hồi từ API x.ai: {}", response);
        log.info("Nội dung phản hồi từ API x.ai: {}", response.getBody());

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                    String content = (String) message.get("content");
                    // Trích xuất phần JSON từ content (nếu có)
                    String jsonPart = extractJson(content);
                    if (jsonPart != null) {
                        return objectMapper.readValue(jsonPart, new TypeReference<Map<String, Object>>() {});
                    } else {
                        log.warn("Không tìm thấy phần JSON hợp lệ trong nội dung: {}", content);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    /**
     * Tổng hợp câu trả lời tư vấn dựa trên câu hỏi của khách hàng và dữ liệu xe.
     * Yêu cầu API x.ai tạo ra câu trả lời cuối cùng.
     */
    public String assembleAnswer(String question, List<Map<String, Object>> carData) {
        String prompt = "Dựa vào dữ liệu xe thuê dưới đây và câu hỏi của khách hàng, hãy tư vấn lựa chọn xe phù hợp.\n"
                + "Câu hỏi: " + question + "\n"
                + "Dữ liệu xe: " + carData.toString() + "\n"
                + "Hãy đưa ra câu trả lời chi tiết với các gợi ý hợp lý.";

        Map<String, Object> payload = new HashMap<>();
//        payload.put("model", "grok-2-latest");
        payload.put("model", "grok-3-mini-beta");
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        payload.put("messages", messages);
        payload.put("temperature", 0.7);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + xaiApiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(xaiApiUrl + "/chat/completions", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                    return (String) message.get("content");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "Xin lỗi, hệ thống không thể tư vấn lúc này.";
    }

    /**
     * Trích xuất chuỗi JSON từ nội dung văn bản nếu có chứa khối JSON.
     * Nếu tìm thấy khối được định dạng bằng ```json ... ```, thì trả về nội dung giữa chúng.
     * Nếu không, cố gắng lấy chuỗi từ dấu '{' đầu tiên đến '}' cuối cùng.
     */
    private String extractJson(String content) {
        Pattern pattern = Pattern.compile("```json\\s*(\\{.*?\\})\\s*```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return content.substring(start, end + 1).trim();
        }
        return null;
    }
}
