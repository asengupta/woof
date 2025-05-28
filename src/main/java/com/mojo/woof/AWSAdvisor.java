package com.mojo.woof;

import com.fasterxml.jackson.core.JsonProcessingException;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.charset.StandardCharsets;

public class AWSAdvisor {
//    private static final Logger LOGGER = Logger.getLogger(AWSAdvisor.class.getName());
    private final String awsAccessKey;
    private final String awsSecretAccessKey;
    private final String awsSessionToken;
    private final Region region;
    private final String modelId;

    public AWSAdvisor(AWSCredentials credentials) {
        this.awsAccessKey = credentials.accessKey();
        this.awsSecretAccessKey = credentials.secretKey();
        this.awsSessionToken = credentials.sessionToken();
        this.region = Region.US_EAST_1;
        this.modelId = "us.anthropic.claude-3-7-sonnet-20250219-v1:0";
    }

    public String advise(String prompt) {// Build Claude-style JSON request

        // Build Claude-style JSON request
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        ArrayNode messages = mapper.createArrayNode();

        messages.addObject()
                .put("role", "user")
                .put("content", prompt);

        body.set("messages", messages);
        body.put("anthropic_version", "bedrock-2023-05-31");
        body.put("max_tokens", 10000);
        System.out.println(body);
        String response = "";
        try {

            // Build Bedrock client with static credentials
            BedrockRuntimeClient bedrockClient = BedrockRuntimeClient.builder()
                    .region(region)
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsSessionCredentials.create(this.awsAccessKey, this.awsSecretAccessKey, this.awsSessionToken)
                    ))
                    .build();

            // Create the invoke request
            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(modelId)
                    .contentType("application/json")
                    .accept("application/json")
                    .body(SdkBytes.fromString(body.toString(), StandardCharsets.UTF_8))
                    .build();

            // Invoke Claude model
            InvokeModelResponse modelResponse = bedrockClient.invokeModel(request);

            // Parse modelResponse
            String reply = modelResponse.body().asUtf8String();
            System.out.println(reply);
             return mapper.readTree(reply)
                    .path("content")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    public static void main(String[] args){

        AWSAdvisor aws_advisor = new AWSAdvisor(AWSCredentials.fromEnv());
        String reply = aws_advisor.advise("Hello, This is a test prompt!");
        System.out.println("Claude replied:\n" + reply);

    }
}
