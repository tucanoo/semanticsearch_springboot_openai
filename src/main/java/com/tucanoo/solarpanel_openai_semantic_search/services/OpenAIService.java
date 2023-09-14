package com.tucanoo.solarpanel_openai_semantic_search.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String OPENAI_EMBEDDINGS_URL = "https://api.openai.com/v1/embeddings";
    private static final String OPENAI_MODERATION_URL = "https://api.openai.com/v1/moderations";

    public float[] getEmbeddings(String input) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        JSONObject body = new JSONObject();
        body.put("input", input);
        body.put("model", "text-embedding-ada-002");

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_EMBEDDINGS_URL, entity, String.class);

        float[] embeddingsAsList;
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            JSONArray embeddings = jsonResponse.getJSONArray("data").getJSONObject(0).getJSONArray("embedding");
            ObjectMapper objectMapper = new ObjectMapper();
            embeddingsAsList = objectMapper.readValue(embeddings.toString(), new TypeReference<float[]>() {
            });
        } else {
            throw new RuntimeException("Failed to get response from OpenAI API");
        }
        return embeddingsAsList;
    }
}
