package com.mojo.woof.llm;

public record OllamaCredentials(String ollamaEndpoint) {

    public static final String OLLAMA_ENDPOINT = "OLLAMA_ENDPOINT";

    public static OllamaCredentials fromEnv() {
        return new OllamaCredentials(System.getenv(OLLAMA_ENDPOINT));
    }
}
