package com.tucanoo.solarpanel_openai_semantic_search.data.model;

import lombok.Data;

@Data
public class FAQ {
    private Long id;
    private String question;
    private String answer;
    private float[] embedding;

    public FAQ(Long id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }
}
