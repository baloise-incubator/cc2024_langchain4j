package org.example.playlangchain4j.otherexamples.embedding.store;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;

// run https://www.trychroma.com/  i.e. via `docker run -p 8000:8000 chromadb/chroma`
class ChromaEmbeddingStoreExampleTest {
	@Test
	void t1() {
		ChromaEmbeddingStore embeddingStore = new ChromaEmbeddingStore("http://localhost:8000", "collection", Duration.ofSeconds(20));

		EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

		embed("I like football.", embeddingModel, embeddingStore);
		embed("The weather is good today.", embeddingModel, embeddingStore);
		embed("Yesterday, the weather was bad.", embeddingModel, embeddingStore);
//		embed("Cats are furry.", embeddingModel, embeddingStore);

		Embedding queryEmbedding = embeddingModel.embed("I do like baseball").content();
		List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 1, 0.5);
		EmbeddingMatch<TextSegment> embeddingMatch = relevant.getFirst();

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

	private static void embed(String text, EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
		TextSegment segment2 = TextSegment.from(text);
		Embedding embedding2 = embeddingModel.embed(segment2).content();
		embeddingStore.add(embedding2, segment2);
	}
}
