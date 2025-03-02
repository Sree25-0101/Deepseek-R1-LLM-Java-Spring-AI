package dev.sreenidhi.ds;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
public class OllamaChatController {

    private final ChatClient chatClient;
    private final ConversationService conversationService;

    public OllamaChatController(@Qualifier("ollamaAiChatClient") ChatClient chatClient, ConversationService conversationService) {
        this.chatClient = chatClient;
        this.conversationService = conversationService;
    }

    @GetMapping("/ollama")
    public Flux<String> ollama(){
        return chatClient.prompt()
                .user("Can you give an example of leet style coding problem and asnwer it in Java.")
                .stream()
                .content();
    }

    @PostMapping("/prompt")
    public Flux<String> promptLlm(@RequestBody PromptRequest request) {

        /*  Just the single prompt and response
        return chatClient.prompt()
                .user(request.getPrompt())
                .stream()
                .content();
         */

        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }

        // Add the user message to history
        conversationService.addUserMessage(sessionId, request.getPrompt());

        // Get full conversation history
        List<ConversationService.ChatMessage> history =
                conversationService.getConversation(sessionId);

        final String finalSessionId = sessionId;
        StringBuilder responseBuilder = new StringBuilder();

        // Format conversation history into a context string
        String contextPrompt = formatConversationContext(history);

        // Stream the response
        return chatClient.prompt()
                .user(contextPrompt)
                .stream()
                .content()
                .doOnNext(chunk -> {
                    // Accumulate the response chunks
                    responseBuilder.append(chunk);
                })
                .doOnComplete(() -> {
                    // Add the complete assistant response to the conversation history
                    conversationService.addAssistantMessage(finalSessionId, responseBuilder.toString());
                });
    }

    // Format conversation history into a single prompt with context
    private String formatConversationContext(List<ConversationService.ChatMessage> history) {
        StringBuilder sb = new StringBuilder();

        // Add context that this is a continuing conversation
        if (history.size() > 2) {
            sb.append("This is a continuing conversation. Previous messages:\n\n");

            // Add all but the last message (which is the current user prompt)
            for (int i = 0; i < history.size() - 1; i++) {
                ConversationService.ChatMessage msg = history.get(i);
                if ("user".equals(msg.getRole())) {
                    sb.append("User: ").append(msg.getContent()).append("\n\n");
                } else {
                    sb.append("Assistant: ").append(msg.getContent()).append("\n\n");
                }
            }

            sb.append("Now respond to the current message:\n");
        }

        // Add the current user message
        if (!history.isEmpty()) {
            sb.append(history.get(history.size() - 1).getContent());
        }

        return sb.toString();
    }

}
