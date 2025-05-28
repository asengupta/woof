package com.mojo.woof.llm;

public record OpenAICredentials(String key, String endpoint) {
    public static OpenAICredentials fromEnv() {
        return new OpenAICredentials(System.getenv("AZURE_OPENAI_API_KEY"), System.getenv("AZURE_OPENAI_ENDPOINT"));
    }
}
