package dev.sreenidhi.ds;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class DeepseekController {

    private final ChatClient chatClient;

    public DeepseekController(@Qualifier("openAiChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/deepseekApi")
    public String deepseek() {
        return chatClient.prompt()
                .user("How many r's are in the word Strawberry")
                .call()
                .content();
    }

    @GetMapping("/deepseek-StreamApi")
    public Flux<String> deepseekStream()  {
        return chatClient.prompt()
                .user("How many r's are in the word  Strawberry")
                .stream()
                .content();
    }
}
