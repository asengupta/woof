package com.mojo.woof.llm;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AzureOpenAIAdvisor implements Advisor {
    private static final Logger LOGGER = Logger.getLogger(AzureOpenAIAdvisor.class.getName());
    private final OpenAIClient client;

    public AzureOpenAIAdvisor(OpenAICredentials credentials) {
        String azureOpenaiApiKey = credentials.key();
        String azureOpenaiEndpoint = credentials.endpoint();
        client = new OpenAIClientBuilder()
                .endpoint(azureOpenaiEndpoint)
                .credential(new AzureKeyCredential(azureOpenaiApiKey))
                .buildClient();
    }

    @Override
    public List<String> advise(String prompt) {
        List<ChatRequestMessage> prompt2 = new ArrayList<>();
        prompt2.add(new ChatRequestUserMessage(prompt));

        ChatCompletions completions = null;
        while (true)
        {
            try {
                String deploymentOrModelId = "gpt35";
                completions = client.getChatCompletions(deploymentOrModelId, new ChatCompletionsOptions(prompt2));
                LOGGER.info(String.format("Model ID=%s is created at %s.", completions.getId(), completions.getCreatedAt()));
                for (ChatChoice choice : completions.getChoices()) {
                    LOGGER.info(String.format("Index: %d, Text: %s.", choice.getIndex(), choice.getMessage().getContent()));
                }

                List<String> responses = completions.getChoices().stream().map(c -> c.getMessage().getContent()).toList();

                CompletionsUsage usage = completions.getUsage();
                LOGGER.info(String.format("Usage: number of prompt token is %d, "
                                + "number of completion token is %d, and number of total tokens in request and response is %d.",
                        usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens()));
                return responses;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Retrying...");
            }
        }
    }
}
