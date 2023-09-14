package com.tucanoo.solarpanel_openai_semantic_search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tucanoo.solarpanel_openai_semantic_search.data.model.FAQ;
import com.tucanoo.solarpanel_openai_semantic_search.services.FAQService;
import com.tucanoo.solarpanel_openai_semantic_search.services.OpenAIService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class FAQServiceTests {

    @Autowired
    FAQService faqService;

    @Autowired
    OpenAIService openAIService;

    @Test
    void testSearchReturnsGoodResult() throws JsonProcessingException {
        // given    - a question and the embedding for the question
        // this particular question should touch several of the FAQs relating to cost and weather
        String question = "I've been thinking about upgrading my roof soon, but I'm also interested in lowering my electricity bills " +
            "and understanding how weather might affect my investment. What should I consider?";

        float[] embeddingForQuestion = openAIService.getEmbeddings(question);

        // when -   we search using our internal similarity search
        List<FAQ> result = faqService.searchFAQUsingInternalSearch(embeddingForQuestion);

        // then - we should receive the top 3 most relevant results
        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
    }

    @Test
    void testIrrelevantSearchReturnsNothing() throws JsonProcessingException {
        // given    - a question and the embedding for the question
        // This question is significantly unrelated to solar panels, so we shouldn't see any related results
        String question = "I can never remember port from starboard, can you remind me which is which?";

        float[] embeddingForQuestion = openAIService.getEmbeddings(question);

        // when -   we search using our internal similarity search
        List<FAQ> result = faqService.searchFAQUsingInternalSearch(embeddingForQuestion);

        // then - we shouldn't receive any results
        Assertions.assertTrue(result.isEmpty());
    }
}
