package org.example.playlangchain4j.otherexamples.embedding.store;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * from: https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/embedding/store/InMemoryEmbeddingStoreExample.java
 */
class JavaSourceExampleTest {
	@Test
	void t1() {
		InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

		EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

		embed("""
			package tech.allegro.hexagon.articles.domain.model;

			public class PersonName {
			    private final String value;

			    private PersonName(final String value) {
			        this.value = value;
			    }

			    public static PersonName of(final String content) {
			        return new PersonName(content);
			    }

			    public String value() {
			        return value;
			    }
			}

			""", embeddingModel, embeddingStore);
		embed("""
package tech.allegro.hexagon.articles.domain;

import tech.allegro.hexagon.articles.domain.model.Article;
import tech.allegro.hexagon.articles.domain.ports.ArticleMessageSender;
import tech.allegro.hexagon.articles.domain.ports.AuthorNotifier;
import tech.allegro.hexagon.articles.domain.ports.SocialMediaPublisher;

import java.util.List;

public class ArticlePublisher {
    private final ArticleMessageSender messageSender;
    private final List<SocialMediaPublisher> socialMediaPublishers;
    private final List<AuthorNotifier> articleAuthorNotifiers;

    public ArticlePublisher(final ArticleMessageSender messageSender,
                            final List<SocialMediaPublisher> socialMediaPublishers,
                            final List<AuthorNotifier> articleAuthorNotifiers) {
        this.messageSender = messageSender;
        this.socialMediaPublishers = socialMediaPublishers;
        this.articleAuthorNotifiers = articleAuthorNotifiers;
    }

    public void publishCreationOf(final Article article) {
        messageSender.sendMessageForCreated(article);
        socialMediaPublishers.forEach(socialMediaPublisher -> socialMediaPublisher.publish(article));
        articleAuthorNotifiers.forEach(articleAuthorNotifier -> articleAuthorNotifier.notifyAboutCreationOf(article));
    }

    public void publishRetrievalOf(final Article article) {
        messageSender.sendMessageForRetrieved(article);
    }
}
			""", embeddingModel, embeddingStore);
//		embed("Yesterday, the weather was bad.", embeddingModel, embeddingStore);

//		Embedding queryEmbedding = embeddingModel.embed("how can I create a PersonName").content();
		Embedding queryEmbedding = embeddingModel.embed("how can I create a ArticlePublisher").content();
		List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 1);
		EmbeddingMatch<TextSegment> embeddingMatch = relevant.get(0);

		System.out.println(embeddingMatch.score()); // 0.8144288515898701
		System.out.println(embeddingMatch.embedded().text()); // I like football.
	}

	private static void embed(String text, EmbeddingModel embeddingModel, InMemoryEmbeddingStore<TextSegment> embeddingStore) {
		TextSegment segment2 = TextSegment.from(text);
		Embedding embedding2 = embeddingModel.embed(segment2).content();
		embeddingStore.add(embedding2, segment2);
	}
}
