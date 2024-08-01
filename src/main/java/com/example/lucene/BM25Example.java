package com.example.lucene;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.tartarus.snowball.ext.EnglishStemmer;



import java.nio.file.Paths;
import java.io.IOException;

public class BM25Example {
    public static void main(String[] args) {
        try {
            // 1. Create an FSDirectory instance to store the index on the file system
            Directory directory = FSDirectory.open(Paths.get("index-directory"));

            // 2. Create a StandardAnalyzer instance
            Analyzer analyzer = new StandardAnalyzer();

            // 3. Set up IndexWriter configuration and create an IndexWriter
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(directory, config);

            // 4. Add documents to the index using OpenNLP for tokenization and EnglishStemmer for stemming
            String[] texts = {
                "The quick brown fox jumps over the lazy dog",
                "Lucene is a powerful Java library for text searching and indexing",
                "Natural Language Processing with Apache OpenNLP is effective for text analysis",
                "The effectiveness of information retrieval systems is evaluated using precision and recall",
                "Stemming reduces words to their root form, improving search accuracy"
            };

            SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
            EnglishStemmer stemmer = new EnglishStemmer();

            for (String text : texts) {
                Document doc = new Document();
                String[] tokens = tokenizer.tokenize(text);
                StringBuilder tokenizedContent = new StringBuilder();
                for (String token : tokens) {
                    String stemmedToken = stem(token, stemmer);
                    tokenizedContent.append(stemmedToken).append(" ");
                }
                doc.add(new TextField("content", tokenizedContent.toString(), Field.Store.YES));
                indexWriter.addDocument(doc);
            }

            // 5. Commit and close the index writer
            indexWriter.commit();
            indexWriter.close();

            // 6. Create an IndexReader and IndexSearcher
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);

            // 7. Set BM25 as the similarity model
            searcher.setSimilarity(new BM25Similarity());

            // 8. Parse the query using OpenNLP for tokenization and EnglishStemmer for stemming
            String queryString = "text search accuracy";
            String[] queryTokens = tokenizer.tokenize(queryString);
            StringBuilder tokenizedQuery = new StringBuilder();
            for (String token : queryTokens) {
                String stemmedToken = stem(token, stemmer);
                tokenizedQuery.append(stemmedToken).append(" ");
            }
            QueryParser parser = new QueryParser("content", analyzer);
            Query query = parser.parse(tokenizedQuery.toString().trim());

            // 9. Search the index
            TopDocs results = searcher.search(query, 5);
            ScoreDoc[] hits = results.scoreDocs;

            // 10. Display the results
            System.out.println("Found " + hits.length + " hits.");
            for (ScoreDoc hit : hits) {
                Document doc = searcher.doc(hit.doc);
                System.out.println("Doc ID: " + hit.doc + " Score: " + hit.score);
                System.out.println("Content: " + doc.get("content"));
            }

            // 11. Close the reader
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String stem(String token, EnglishStemmer stemmer) {
        stemmer.setCurrent(token);
        stemmer.stem();
        return stemmer.getCurrent();
    }
}
