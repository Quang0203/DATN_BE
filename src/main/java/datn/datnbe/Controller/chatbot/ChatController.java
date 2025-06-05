package datn.datnbe.Controller.chatbot;

import datn.datnbe.Service.chatbot.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatbotService chatbotService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> payload) {
        long startTime = System.currentTimeMillis(); // Bắt đầu đo tổng thời gian
        System.out.println("Received payload: " + payload);
        String question = payload.get("question");
        String answer = chatbotService.handleChat(question);
        long endTime = System.currentTimeMillis(); // Kết thúc đo tổng thời gian
        System.out.println("Tổng thời gian xử lý API: " + (endTime - startTime) + " ms");
        return ResponseEntity.ok(Collections.singletonMap("answer", answer));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/with-history")
    public ResponseEntity<Map<String, String>> chatWithHistory(@RequestBody Map<String, List<Map<String, String>>> payload) {
        long startTime = System.currentTimeMillis();
        System.out.println("Received payload: " + payload);
        List<Map<String, String>> messages = payload.get("messages");
        String answer = chatbotService.handleChatWithHistory(messages);
        long endTime = System.currentTimeMillis();
        System.out.println("Tổng thời gian xử lý API: " + (endTime - startTime) + " ms");
        return ResponseEntity.ok(Map.of("answer", answer));
    }
}