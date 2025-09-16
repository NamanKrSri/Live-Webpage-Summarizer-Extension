package com.research.WebSummarizer_assistant.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.research.WebSummarizer_assistant.Components.GeminiResponse;
import com.research.WebSummarizer_assistant.Components.ResearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
/*
Process flow-
1-Build Prompt
2-Query the AI model API
3-Parse the response
4-Return response
*/

@Service
public class ResearchService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    @Value("${gemini.api.key}")
    private String geminiApiKey;
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    public ResearchService(WebClient.Builder webClientBuilder,ObjectMapper objectMapper) {
        this.objectMapper=objectMapper;
        this.webClient = WebClient.builder().build();
    }

    public String processContent(ResearchRequest researchRequest) {
        //step1
        String prompt=BuildPrompt(researchRequest);
        //step2
        Map<String,Object> requestMap=buildApiRequestBodyMap(prompt);
        //step3
        String JsonResponse=webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .bodyValue(requestMap)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        //step 4
        return extractTextFromResponse(JsonResponse);
    }

    private String extractTextFromResponse(String JsonResponse) {
        String textresponse=null;
        try {
            GeminiResponse geminiResponse=objectMapper.readValue(JsonResponse,GeminiResponse.class);
            if(geminiResponse.getCandidates()!=null && !geminiResponse.getCandidates().isEmpty()){
                GeminiResponse.Candidates firstCandidate=geminiResponse.getCandidates().get(0);
                if(firstCandidate.getContent()!=null && firstCandidate.getContent().getParts()!=null){
                     textresponse= firstCandidate.getContent().getParts().get(0).getText();
                }
            }

        }catch (Exception e){
            return "Error Parsing"+e.getMessage();
        }
        return textresponse;
    }

    public Map<String, Object> buildApiRequestBodyMap(String prompt) {
        Map<String, Object> map= Map.of("contents",new Object[]{
                Map.of("parts",new Object[]{
                        Map.of("text",prompt)
                })
        });
        return map;
    }

    public String BuildPrompt(ResearchRequest researchRequest){
        StringBuilder prompt=new StringBuilder();
        switch (researchRequest.getOperation()){
            case "summarize":
                prompt.append("Provide the clear and concise summary of the following statement in very simple language but cover all the topics \n\n");
                break;
            case "suggest":
                prompt.append("suggest the most relevant content related to the following statement\n\n");
                break;
            default:
                throw new IllegalArgumentException("Unknown Operation"+ researchRequest.getContent());
        }
        prompt.append(researchRequest.getContent());
        return prompt.toString();
    }
}
