package com.research.WebSummarizer_assistant.Components;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
public class ResearchRequest {
    private String content;
    private String operation;
}
