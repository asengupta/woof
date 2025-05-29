package com.mojo.woof.llm;

import com.google.common.collect.ImmutableList;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
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
import java.util.List;

public class AWSAdvisor implements Advisor {
    public static final String ANTHROPIC_VERSION = "anthropic_version";
    public static final String MAX_TOKENS = "max_tokens";
    public static final String BEDROCK_2023_05_31 = "bedrock-2023-05-31";
    //    private static final Logger LOGGER = Logger.getLogger(AWSAdvisor.class.getName());
    private final String awsAccessKey;
    private final String awsSecretAccessKey;
    private final Region region;
    private final String modelId;

    public AWSAdvisor(AWSCredentials credentials) {
        this.awsAccessKey = credentials.accessKey();
        this.awsSecretAccessKey = credentials.secretKey();
        this.region = Region.of(credentials.region());
        this.modelId = credentials.modelID();
    }

    public List<String> advise(String prompt) {// Build Claude-style JSON request

        // Build Claude-style JSON request
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        ArrayNode messages = mapper.createArrayNode();

        messages.addObject()
                .put("role", "user")
                .put("content", prompt);

        body.set("messages", messages);
        body.put(ANTHROPIC_VERSION, BEDROCK_2023_05_31);
        body.put(MAX_TOKENS, 10000);
        System.out.println(body);
        String response = "";
        try {

            BedrockRuntimeClient bedrockClient = BedrockRuntimeClient.builder()
                    .region(this.region)
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(this.awsAccessKey, this.awsSecretAccessKey)
                            )
                    )
                    .build();

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(modelId)
                    .contentType("application/json")
                    .accept("application/json")
                    .body(SdkBytes.fromString(body.toString(), StandardCharsets.UTF_8))
                    .build();

            InvokeModelResponse modelResponse = bedrockClient.invokeModel(request);

            String reply = modelResponse.body().asUtf8String();
            System.out.println(reply);
             return ImmutableList.of(mapper.readTree(reply)
                    .path("content")
                    .get(0)
                    .path("text")
                    .asText());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args){

        AWSAdvisor aws_advisor = new AWSAdvisor(AWSCredentials.fromEnv());
        List<String> reply = aws_advisor.advise("Hello, This is a test prompt!");
        System.out.println("Claude replied:\n" + reply);

    }
}
