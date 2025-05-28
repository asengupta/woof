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
import java.util.logging.Logger;

public class OllamaAdvisor implements Advisor {
    private static final Logger LOGGER = Logger.getLogger(OllamaAdvisor.class.getName());
    private final HttpClient client;

    public OllamaAdvisor(HttpClient client) {
        this.client = client;
    }

    public OllamaAdvisor() {
        this(HttpClient.newHttpClient());
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
                .uri(URI.create("http://localhost:11434/api/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonObject = new Gson().fromJson(response.body(), JsonObject.class);
            return ImmutableList.of(jsonObject.get("response").getAsString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
