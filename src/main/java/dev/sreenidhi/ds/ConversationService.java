package dev.sreenidhi.ds;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConversationService {

    // Using a Map to store conversation histories, with sessionId as the key
    private final Map<String, List<ChatMessage>> conversations = new ConcurrentHashMap<>();

    // A simple class to represent chat messages
    public static class ChatMessage {
        private final String role; // "user" or "assistant"
        private final String content;

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }

    // Add a user message to a conversation
    public void addUserMessage(String sessionId, String message) {
        conversations.computeIfAbsent(sessionId, k -> new ArrayList<>())
                .add(new ChatMessage("user", message));
    }

    // Add an assistant message to a conversation
    public void addAssistantMessage(String sessionId, String message) {
        conversations.computeIfAbsent(sessionId, k -> new ArrayList<>())
                .add(new ChatMessage("assistant", message));
    }

    // Get the entire conversation history for a session
    public List<ChatMessage> getConversation(String sessionId) {
        return conversations.getOrDefault(sessionId, new ArrayList<>());
    }

}