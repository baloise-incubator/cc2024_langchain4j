package org.example.playlangchain4j.otherexamples.embedding.store;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

/**
 * from: https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/embedding/store/InMemoryEmbeddingStoreExample.java
 */
class TutorialExampleTest {
	final String OPENAI_API_KEY = "demo";
	@Test
	void save_store() {
		List<Document> documents = FileSystemDocumentLoader.loadDocumentsRecursively(
			"/home/se/se/sweng/0_daily/2022/2022-03-20_hexagonal-arch-spring/github/dziadeusz/hexagonal-architecture-by-example/src/main/java"
		);
		System.out.println("documents = " + documents.size());

		InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
		EmbeddingStoreIngestor.ingest(documents, embeddingStore);
		String filePath = "./0_embedding.store";
		embeddingStore.serializeToFile(filePath);

	}

	@Test
	void t1() {
		InMemoryEmbeddingStore<TextSegment> embeddingStore = InMemoryEmbeddingStore.fromFile(Path.of("./0_embedding.store"));

		Assistant assistant = AiServices.builder(Assistant.class)
			.chatLanguageModel(OpenAiChatModel.withApiKey(OPENAI_API_KEY))
			.chatMemory(MessageWindowChatMemory.withMaxMessages(10))
			.contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
			.build();

		Result<String> result = assistant.chat("what does the DbArticleRepository do?");
		String answer = result.content();
		List<Content> sources = result.sources();
		System.out.println(answer);
		System.out.println(sources);
	}

}
