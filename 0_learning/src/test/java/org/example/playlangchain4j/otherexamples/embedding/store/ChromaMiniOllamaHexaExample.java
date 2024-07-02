package org.example.playlangchain4j.otherexamples.embedding.store;

import java.time.Duration;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;

//see https://ollama.com/download/linux
//
//
//curl -fsSL https://ollama.com/install.sh | sh
//
//see https://github.com/ollama/ollama/issues/729#issuecomment-1906311485
//
//sudi vi  /etc/systemd/system/ollama.service
//
//[Service]
//Environment="https_proxy=http://<IP>:<PORT>"

//sudo systemctl daemon-reload
//sudo systemctl restart ollama.service
//
//ollama pull codegemma
//ollama run codegemma

public class ChromaMiniOllamaHexaExample {

    interface Assistant {

        TokenStream chat(String userMessage);
    }

    public static void main(String[] args) {
        //Dotenv dotenv = Dotenv.load();

        EmbeddingStore<TextSegment> embeddingStore = ChromaEmbeddingStore.builder()
                .baseUrl("http://localhost:8000")
                .collectionName("hexa_all-MiniLM-L6-v2")
                .build();
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        StreamingChatLanguageModel model = OllamaStreamingChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("codegemma")
                .temperature(1.0)
                .timeout(Duration.ofSeconds(600))
                .build();

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

        TokenStream tokenStream = assistant.chat("how can I publish an article on twitter?");
        tokenStream.onNext(System.out::print)
                .onComplete(a -> System.out.println("\ndone"))
                .onError(Throwable::printStackTrace)
                .start();
    }
}
