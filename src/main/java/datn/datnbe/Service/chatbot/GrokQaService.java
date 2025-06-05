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
//    public Map<String, Object> analyzeQuery(String question, List<String> carFields) {
//        long startTime = System.currentTimeMillis(); // Bắt đầu đo tổng thời gian của analyzeQuery
//
//        String prompt = "Phân tích câu hỏi liên quan đến thuê xe: \"" + question + "\"\n"
//                + "Danh sách các field liên quan: " + carFields.toString() + "\n"
//                + "Trả về một JSON object chứa các tiêu chí tìm kiếm phù hợp. Ví dụ: {\"brand\": \"Toyota\", \"model\": \"Innova\", \"numberofseats\": 5, \"baseprice\": \"600\", \"status\": \"available\"}";
//
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("model", "grok-3-mini-fast-latest");
//        List<Map<String, String>> messages = new ArrayList<>();
//        messages.add(Map.of("role", "user", "content", prompt));
//        payload.put("messages", messages);
//        payload.put("temperature", 0.0);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "Bearer " + xaiApiKey);
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
//        log.info("URL đến API x.ai: {}", request);
//
//        long apiStart = System.currentTimeMillis();
//        ResponseEntity<Map> response = restTemplate.postForEntity(xaiApiUrl + "/chat/completions", request, Map.class);
//        long apiEnd = System.currentTimeMillis();
//        log.info("Thời gian gọi API x.ai: {} ms", (apiEnd - apiStart));
//
//        log.info("Phản hồi từ API x.ai: {}", response);
////        log.info("Nội dung phản hồi từ API x.ai: {}", response.getBody());
//
//        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//            try {
//                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
//                if (choices != null && !choices.isEmpty()) {
//                    Map<String, Object> firstChoice = choices.get(0);
//                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
//                    String content = (String) message.get("content");
//                    long extractStart = System.currentTimeMillis();
//                    String jsonPart = extractJson(content);
//                    long extractEnd = System.currentTimeMillis();
//                    log.info("Thời gian trích xuất JSON trong analyzeQuery: {} ms", (extractEnd - extractStart));
//                    if (jsonPart != null) {
//                        Map<String, Object> result = objectMapper.readValue(jsonPart, new TypeReference<Map<String, Object>>() {});
//                        long endTime = System.currentTimeMillis();
//                        log.info("Tổng thời gian xử lý analyzeQuery: {} ms", (endTime - startTime));
//                        return result;
//                    } else {
//                        log.warn("Không tìm thấy phần JSON hợp lệ trong nội dung: {}", content);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        long endTime = System.currentTimeMillis();
//        log.info("Tổng thời gian xử lý analyzeQuery: {} ms", (endTime - startTime));
//        return new HashMap<>();
//    }

    /**
     * Tổng hợp câu trả lời tư vấn dựa trên câu hỏi của khách hàng và dữ liệu xe.
     * Yêu cầu API x.ai tạo ra câu trả lời cuối cùng.
     */
//    public String assembleAnswer(String question, List<Map<String, Object>> carData) {
//        long startTime = System.currentTimeMillis(); // Bắt đầu đo tổng thời gian của assembleAnswer
//
//        String prompt = "Dựa vào dữ liệu xe thuê dưới đây và câu hỏi của khách hàng, hãy tư vấn lựa chọn xe phù hợp nhưng không quá dài.\n"
//                + "Câu hỏi: " + question + "\n"
//                + "Dữ liệu xe: " + carData.toString() + "\n"
//                + "Hãy đưa ra câu trả lời chi tiết với các gợi ý hợp lý.";
//
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("model", "grok-3-mini-fast-latest");
//        List<Map<String, String>> messages = new ArrayList<>();
//        messages.add(Map.of("role", "user", "content", prompt));
//        payload.put("messages", messages);
//        payload.put("temperature", 0.3);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "Bearer " + xaiApiKey);
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
//        log.info("Request đến API x.ai: {}", request);
//
//        long apiStart = System.currentTimeMillis();
//        ResponseEntity<Map> response = restTemplate.postForEntity(xaiApiUrl + "/chat/completions", request, Map.class);
//        long apiEnd = System.currentTimeMillis();
//        log.info("Thời gian gọi API x.ai trong assembleAnswer: {} ms", (apiEnd - apiStart));
//
//        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//            try {
//                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
//                if (choices != null && !choices.isEmpty()) {
//                    Map<String, Object> firstChoice = choices.get(0);
//                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
//                    String result = (String) message.get("content");
//                    long endTime = System.currentTimeMillis();
//                    log.info("Tổng thời gian xử lý assembleAnswer: {} ms", (endTime - startTime));
//                    return result;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        long endTime = System.currentTimeMillis();
//        log.info("Tổng thời gian xử lý assembleAnswer: {} ms", (endTime - startTime));
//        return "Xin lỗi, hệ thống không thể tư vấn lúc này.";
//    }

    public Map<String, Object> analyzeQuery(String question, List<String> carFields) {
        long startTime = System.currentTimeMillis();

        String prompt = "Phân tích câu hỏi liên quan đến thuê xe: \"" + question + "\"\n"
                + "Danh sách các field liên quan: " + carFields.toString() + "\n"
                + "Trả về một JSON object chứa các tiêu chí tìm kiếm phù hợp. Ví dụ: {\"brand\": \"Toyota\", \"model\": \"Innova\", \"numberofseats\": 5, \"baseprice\": \"600\", \"status\": \"available\"}";

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "grok-3-mini-fast-latest");
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        payload.put("messages", messages);
        payload.put("temperature", 0.0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + xaiApiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        long apiStart = System.currentTimeMillis();
        ResponseEntity<Map> response = restTemplate.postForEntity(xaiApiUrl + "/chat/completions", request, Map.class);
        long apiEnd = System.currentTimeMillis();
        log.info("Thời gian gọi API x.ai trong analyzeQuery: {} ms", (apiEnd - apiStart));

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                    String content = (String) message.get("content");
                    long extractStart = System.currentTimeMillis();
                    String jsonPart = extractJson(content);
                    long extractEnd = System.currentTimeMillis();
                    log.info("Thời gian trích xuất JSON trong analyzeQuery: {} ms", (extractEnd - extractStart));
                    if (jsonPart != null) {
                        Map<String, Object> result = objectMapper.readValue(jsonPart, new TypeReference<Map<String, Object>>() {});
                        long endTime = System.currentTimeMillis();
                        log.info("Tổng thời gian xử lý analyzeQuery: {} ms", (endTime - startTime));
                        return result;
                    } else {
                        log.warn("Không tìm thấy phần JSON hợp lệ trong nội dung: {}", content);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("Tổng thời gian xử lý analyzeQuery: {} ms", (endTime - startTime));
        return new HashMap<>();
    }

    public String assembleAnswer(String prompt, List<Map<String, Object>> carData) {
        long startTime = System.currentTimeMillis();

        String fullPrompt = "Dựa vào dữ liệu xe thuê dưới đây và cuộc trò chuyện trước đó, hãy tư vấn lựa chọn xe phù hợp.\n"
                + "Cuộc trò chuyện: " + prompt + "\n"
                + "Dữ liệu xe: " + carData.toString() + "\n"
                + "Hãy đưa ra câu trả lời chi tiết với các gợi ý hợp lý.";

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "grok-3-mini-fast-latest");
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", fullPrompt));
        payload.put("messages", messages);
        payload.put("temperature", 0.3);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + xaiApiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        long apiStart = System.currentTimeMillis();
        ResponseEntity<Map> response = restTemplate.postForEntity(xaiApiUrl + "/chat/completions", request, Map.class);
        long apiEnd = System.currentTimeMillis();
        log.info("Thời gian gọi API x.ai trong assembleAnswer: {} ms", (apiEnd - apiStart));

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                    String result = (String) message.get("content");
                    long endTime = System.currentTimeMillis();
                    log.info("Tổng thời gian xử lý assembleAnswer: {} ms", (endTime - startTime));
                    return result;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("Tổng thời gian xử lý assembleAnswer: {} ms", (endTime - startTime));
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
