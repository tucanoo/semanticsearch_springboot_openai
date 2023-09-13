package com.tucanoo.solarpanel_openai_semantic_search.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tucanoo.solarpanel_openai_semantic_search.data.model.FAQ;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FAQService {

    @Value("classpath:providedFAQs.json")
    private Resource faqEmbeddingsResource;

    private final ObjectMapper objectMapper;

    private final OpenAIService openAIService;
    private final InternalSearchService internalSearchService;

    private final List<FAQ> faqList = new ArrayList<>();

    @PostConstruct
    private void init() throws JsonProcessingException {
        faqList.addAll(loadFAQsFromJsonFile());
        generateEmbeddingsJson();
    }

    public List<FAQ> searchFAQUsingInternalSearch(float[] prompt) {

        List<float[]> faqEmbeddings = new ArrayList<>();
        for (FAQ faq : faqList) {
            faqEmbeddings.add(faq.getEmbedding());
        }

        List<Integer> mostSimilarIndices = internalSearchService.findMostSimilarEmbeddings(prompt, faqEmbeddings, 3);

        List<FAQ> topFAQs = new ArrayList<>();
        for (int index : mostSimilarIndices) {
            topFAQs.add(faqList.get(index));
        }

        return topFAQs;
    }

    private List<FAQ> loadFAQsFromJsonFile() {

        try {
            InputStream inputStream = faqEmbeddingsResource.getInputStream();
            FAQ[] faqs = objectMapper.readValue(inputStream, FAQ[].class);

            return List.of(faqs);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void generateEmbeddingsJson() throws JsonProcessingException {

        for (FAQ faq : faqList) {
            // retrieve a vector embedding from openai for the question
            float[] embeddingsAsList = openAIService.getEmbeddings(faq.getQuestion());

            // set the embedding to the faq
            faq.setEmbedding(embeddingsAsList);
        }
    }




}
