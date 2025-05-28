package com.mojo.woof.llm;

public record AWSCredentials(String accessKey, String secretKey, String sessionToken, String modelID, String region) {

    public static final String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";
    public static final String AWS_SECRET_ACCESS_KEY = "AWS_SECRET_ACCESS_KEY";
    public static final String AWS_SESSION_TOKEN = "AWS_SESSION_TOKEN";
    public static final String AWS_MODEL_ID = "AWS_MODEL_ID";
    public static final String AWS_REGION = "AWS_REGION";

    public static AWSCredentials fromEnv() {
        return new AWSCredentials(System.getenv(AWS_ACCESS_KEY_ID),
                System.getenv(AWS_SECRET_ACCESS_KEY),
                System.getenv(AWS_SESSION_TOKEN),
                System.getenv(AWS_MODEL_ID),
                System.getenv(AWS_REGION)
        );
    }
}
