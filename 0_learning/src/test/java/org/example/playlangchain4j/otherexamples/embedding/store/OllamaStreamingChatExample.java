package org.example.playlangchain4j.otherexamples.embedding.store;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.CompletableFuture;

public class OllamaStreamingChatExample {

  static String MODEL_NAME = "orca-mini"; // try "mistral", "llama2", "codellama" or "phi"
  static String DOCKER_IMAGE_NAME = "langchain4j/ollama-" + MODEL_NAME + ":latest";

  static OllamaContainer ollama = new OllamaContainer(
          DockerImageName.parse(DOCKER_IMAGE_NAME).asCompatibleSubstituteFor("ollama/ollama"));

  public static void main(String[] args) {
    ollama.start();
    StreamingChatLanguageModel model = OllamaStreamingChatModel.builder()
        .baseUrl(String.format("http://%s:%d", ollama.getHost(), ollama.getFirstMappedPort()))
        .modelName(MODEL_NAME)
        .temperature(0.0)
        .build();

    String userMessage = "Write a 100-word poem about Java and AI";

    CompletableFuture<Response<AiMessage>> futureResponse = new CompletableFuture<>();
    model.generate(userMessage, new StreamingResponseHandler<AiMessage>() {

      @Override
      public void onNext(String token) {
        System.out.print(token);
      }

      @Override
      public void onComplete(Response<AiMessage> response) {
        futureResponse.complete(response);
      }

      @Override
      public void onError(Throwable error) {
        futureResponse.completeExceptionally(error);
      }
    });

    futureResponse.join();
    ollama.stop();
  }
}
