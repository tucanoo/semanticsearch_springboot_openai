package com.tucanoo.solarpanel_openai_semantic_search.services;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class InternalSearchService {

    private static final double SIMILARITY_THRESHOLD = 0.8; // Just an example threshold to limit the results

    public static List<Integer> findMostSimilarEmbeddings(float[] queryEmbedding, List<float[]> faqEmbeddings, int topResults) {
        List<Double> similarities = new ArrayList<>();
        for (float[] faqEmbedding : faqEmbeddings) {
            double similarity = cosineSimilarity(queryEmbedding, faqEmbedding);
            similarities.add(similarity);
        }

        List<Integer> mostSimilarIndices = new ArrayList<>();
        for (int i = 0; i < faqEmbeddings.size(); i++) {

            // Only consider indices with similarity above the threshold
            if (similarities.get(i) >= SIMILARITY_THRESHOLD)
                mostSimilarIndices.add(i);
        }

        mostSimilarIndices.sort(Comparator.comparingDouble(similarities::get).reversed());

        return mostSimilarIndices.subList(0, Math.min(topResults, mostSimilarIndices.size()));
    }

    private static double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

}
