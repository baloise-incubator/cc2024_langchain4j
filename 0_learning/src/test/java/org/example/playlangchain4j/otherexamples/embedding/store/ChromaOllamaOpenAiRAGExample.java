package org.example.playlangchain4j.otherexamples.embedding.store;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import io.github.cdimascio.dotenv.Dotenv;

public class ChromaOllamaOpenAiRAGExample {

    interface Assistant {

        TokenStream chat(String userMessage);
    }

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        EmbeddingStore<TextSegment> embeddingStore = ChromaEmbeddingStore.builder()
                .baseUrl("http://localhost:8000")
                .collectionName("langchain")
                .build();

        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.withApiKey(dotenv.get("OPENAI_API_KEY"));

        StreamingChatLanguageModel model = OllamaStreamingChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3")
                .temperature(1.0)
                .build();
//        StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
//                .apiKey(dotenv.get("OPENAI_API_KEY"))
//                .modelName("gpt-4")
//                .build();

        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(8)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .streamingChatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(contentRetriever)
                .build();

        TokenStream tokenStream = assistant.chat("what's the max length of a street?");
        tokenStream.onNext(System.out::print)
                .onComplete(a -> System.out.println("\ndone"))
                .onError(Throwable::printStackTrace)
                .start();
    }
}