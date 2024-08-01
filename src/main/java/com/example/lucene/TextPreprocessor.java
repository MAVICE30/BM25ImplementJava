package com.example.lucene;

import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.SimpleTokenizer;

public class TextPreprocessor {
    private SimpleTokenizer tokenizer;
    private PorterStemmer stemmer;

    public TextPreprocessor() {
        tokenizer = SimpleTokenizer.INSTANCE;
        stemmer = new PorterStemmer();
    }

    public String preprocess(String text) {
        String[] tokens = tokenizer.tokenize(text);
        StringBuilder preprocessedText = new StringBuilder();
        for (String token : tokens) {
            String stem = stemmer.stem(token);
            preprocessedText.append(stem).append(" ");
        }
        return preprocessedText.toString().trim();
    }
}