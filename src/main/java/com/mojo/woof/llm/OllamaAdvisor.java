package com.mojo.woof.llm;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class OllamaAdvisor implements Advisor {
    private final HttpClient client;
    private final String ollamaEndpoint;

    public OllamaAdvisor(HttpClient client, OllamaCredentials credentials) {
        this.client = client;
        ollamaEndpoint = credentials.ollamaEndpoint();
    }

    public OllamaAdvisor(OllamaCredentials credentials) {
        this(HttpClient.newHttpClient(), credentials);
    }

    @Override
    public List<String> advise(String prompt) {
        String payload = String.format("""
                {
                  "model": "mistral",
                  "prompt": "%s",
                  "stream": false
                }
                """, prompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ollamaEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonObject = new Gson().fromJson(response.body(), JsonObject.class);
            return ImmutableList.of(jsonObject.get("response").getAsString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
