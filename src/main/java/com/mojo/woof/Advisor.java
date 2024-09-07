package com.mojo.woof;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Advisor {
    private static final Logger LOGGER = Logger.getLogger(Advisor.class.getName());
    public static final String AZURE_OPENAI_ENDPOINT = "AZURE_OPENAI_ENDPOINT";
    public static final String AZURE_OPENAI_API_KEY = "AZURE_OPENAI_API_KEY";
    private final String azureOpenaiApiKey;
    private final String azureOpenaiEndpoint;
    private final OpenAIClient client;
    private final String deploymentOrModelId = "gpt35";

    public Advisor(OpenAICredentials credentials) {
        this.azureOpenaiApiKey = credentials.key();
        this.azureOpenaiEndpoint = credentials.endpoint();
        client = new OpenAIClientBuilder()
                .endpoint(azureOpenaiEndpoint)
                .credential(new AzureKeyCredential(azureOpenaiApiKey))
                .buildClient();
    }

    public List<String> advise(String prompt) {
        List<ChatRequestMessage> prompt2 = new ArrayList<>();
        prompt2.add(new ChatRequestUserMessage(prompt));

        ChatCompletions completions = client.getChatCompletions(deploymentOrModelId, new ChatCompletionsOptions(prompt2));

        LOGGER.info(String.format("Model ID=%s is created at %s.%n", completions.getId(), completions.getCreatedAt()));
        for (ChatChoice choice : completions.getChoices()) {
            LOGGER.info(String.format("Index: %d, Text: %s.%n", choice.getIndex(), choice.getMessage().getContent()));
        }

        List<String> responses = completions.getChoices().stream().map(c -> c.getMessage().getContent()).toList();

        CompletionsUsage usage = completions.getUsage();
        LOGGER.info(String.format("Usage: number of prompt token is %d, "
                        + "number of completion token is %d, and number of total tokens in request and response is %d.",
                usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens()));
        return responses;
    }
}
