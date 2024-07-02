package org.example.playlangchain4j.otherexamples.embedding.store;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/**
 * from: https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/embedding/store/InMemoryEmbeddingStoreExample.java
 */
class InMemoryEmbeddingStoreExampleTest {
	@Test
	void t1() {
		InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

		EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

		embed("I like football.", embeddingModel, embeddingStore);
		embed("The weather is good today.", embeddingModel, embeddingStore);
		embed("Yesterday, the weather was bad.", embeddingModel, embeddingStore);
//		embed("Cats are furry.", embeddingModel, embeddingStore);

		Embedding queryEmbedding = embeddingModel.embed("I do like baseball").content();
		EmbeddingSearchResult<TextSegment> relevant = embeddingStore.search(
				EmbeddingSearchRequest.builder()
					.queryEmbedding(queryEmbedding)
					.maxResults(1)
					.build()
				);
	
		EmbeddingMatch<TextSegment> embeddingMatch = relevant.matches().get(0);

		System.out.println(embeddingMatch.score()); // 0.8144288515898701
		System.out.println(embeddingMatch.embedded().text()); // I like football.


		// In-memory embedding store can be serialized and deserialized to/from JSON
		// String serializedStore = embeddingStore.serializeToJson();
		// InMemoryEmbeddingStore<TextSegment> deserializedStore = InMemoryEmbeddingStore.fromJson(serializedStore);

		// In-memory embedding store can be serialized and deserialized to/from file
		// String filePath = "/home/me/embedding.store";
		// embeddingStore.serializeToFile(filePath);
		// InMemoryEmbeddingStore<TextSegment> deserializedStore = InMemoryEmbeddingStore.fromFile(filePath);
	}

	private static void embed(String text, EmbeddingModel embeddingModel, InMemoryEmbeddingStore<TextSegment> embeddingStore) {
		TextSegment segment2 = TextSegment.from(text);
		Embedding embedding2 = embeddingModel.embed(segment2).content();
		embeddingStore.add(embedding2, segment2);
	}
}
