package com.mojo.woof;

public record AWSCredentials(String accessKey, String secretKey, String sessionToken){
    public static AWSCredentials fromEnv(){
        return new AWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY"), System.getenv("AWS_SESSION_TOKEN"));
    }
}
