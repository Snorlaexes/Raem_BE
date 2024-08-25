package com.snorlaexes.raem.domain.OpenAI;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatGPTRequest {
    private String model;
    private List<Message> messages;

    public ChatGPTRequest(String model, String prompt) {
        this.model = model;
        this.messages =  new ArrayList<>();
        this.messages.add(new Message("system", "You are an expert sleep coach and data analyst. Your job is to analyze sleep data and provide users with insights about their sleep patterns and personalized recommendations for improvement. When responding, always structure your response into two sections: <Sleep Patterns> and <Improvements and Goals>. Each section should be concise and limited to 150 words or less in Korean. Your responses should be informative, supportive, and encourage healthy sleep habits."));
        this.messages.add(new Message("user", prompt));
    }
}
