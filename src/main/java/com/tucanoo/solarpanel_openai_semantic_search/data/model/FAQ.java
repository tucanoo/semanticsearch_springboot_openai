package com.tucanoo.solarpanel_openai_semantic_search.data.model;

import lombok.Data;
import lombok.ToString;

@Data
public class FAQ {
    private String question;
    private String answer;

    @ToString.Exclude
    private float[] embedding;

    public FAQ(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}
