package com.gruppe.iso.spring_rag_ai.controller;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/rag")
public class RagAiController {

    private final ChatClient client;
    private final VectorStore vectorStore;
    @Value("classpath:/prompts/spring-boot-reference.st")
    private Resource sbPromptTemplate;

    public RagAiController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.client = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }


    @GetMapping(path = "openai")
    public String openai(@RequestParam(value = "message") String message) {
        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.builder().query(message).similarityThreshold(0.7).build());
        if(similarDocuments == null) {
            throw new RuntimeException("Similar docs are null");
        }
        List<String> contentList = similarDocuments.stream().map(Document::getText).toList();
        PromptTemplate promptTemplate = new PromptTemplate(sbPromptTemplate);
        var promptParams = new HashMap<String, Object>();
        promptParams.put("input", message);
        promptParams.put("documents", String.join("\n", contentList));
        Prompt prompt = promptTemplate.create(promptParams);
        ChatResponse chatResponse = client.prompt(prompt).call().chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }
}
