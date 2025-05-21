package datn.datnbe.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatSession {
    private List<Map<String, String>> messages = new ArrayList<>();

    public List<Map<String, String>> getMessages() {
        return messages;
    }

    public void addMessage(String role, String content) {
        // role có thể là "user", "assistant" hoặc "system"
        messages.add(Map.of("role", role, "content", content));
    }

    public void clear() {
        messages.clear();
    }
}