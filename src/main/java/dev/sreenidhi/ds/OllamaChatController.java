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

@RestController
public class OllamaChatController {

    private final ChatClient chatClient;

    public OllamaChatController(@Qualifier("ollamaAiChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
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
        return chatClient.prompt()
                .user(request.getPrompt())
                .stream()
                .content();
    }

}
