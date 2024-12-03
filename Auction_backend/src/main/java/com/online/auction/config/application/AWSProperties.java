package com.online.auction.config.application;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "aws")
public class AWSProperties {

    public String accountId;
    public String bucketName;
    public String region;
    public String accessKey;
    public String secretKey;
    public String sessionToken;
    private String topicArn;
    private String welcomeTopicArn;
}
