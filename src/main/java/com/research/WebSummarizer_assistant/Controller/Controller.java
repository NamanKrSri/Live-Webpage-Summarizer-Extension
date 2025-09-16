package com.research.WebSummarizer_assistant.Controller;

import com.research.WebSummarizer_assistant.Components.ResearchRequest;
import com.research.WebSummarizer_assistant.Service.ResearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/research")
@CrossOrigin(origins = "*")
public class Controller {
    @Autowired
    public ResearchService researchService;
    @PostMapping("process-request")
    public ResponseEntity<String> researchContent(@RequestBody ResearchRequest researchRequest){
        String response=researchService.processContent(researchRequest);
        return ResponseEntity.ok(response);
    }
}
