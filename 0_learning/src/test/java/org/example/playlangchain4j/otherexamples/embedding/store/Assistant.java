package org.example.playlangchain4j.otherexamples.embedding.store;

import dev.langchain4j.service.Result;
import dev.langchain4j.service.TokenStream;

public interface Assistant {
	Result<String> chat(String userMessage);
}
