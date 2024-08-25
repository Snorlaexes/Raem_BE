package com.snorlaexes.raem.domain.OpenAI;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GPTService {
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @Value("${open-api.chat-gpt.api-key}")
    private String apiKey;

    public RestTemplate template(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + apiKey);
            return execution.execute(request, body);
        });
        return restTemplate;
    }

    public String askChatGPT(String userMessage){
        ChatGPTRequest request = new ChatGPTRequest("gpt-4o-2024-08-06", userMessage);
        ChatGPTResponse chatGPTResponse =  template().postForObject(OPENAI_API_URL, request, ChatGPTResponse.class);
        return chatGPTResponse.getChoices().get(0).getMessage().getContent();
    }
}
