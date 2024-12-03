package com.online.auction.service.impl;

import com.online.auction.config.application.AWSProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

@Service
@AllArgsConstructor
@Slf4j
public class SNSService {

    private final SnsClient snsClient;
    private final AWSProperties awsProperties;

    public void subscribeUserToNotifications(String userEmail) {
        try {
            // Subscribe user to general notifications topic
            SubscribeRequest subscribeRequest = SubscribeRequest.builder()
                    .protocol("email")
                    .endpoint(userEmail)
                    .returnSubscriptionArn(true)
                    .topicArn(awsProperties.getTopicArn())
                    .build();

            SubscribeResponse subscribeResponse = snsClient.subscribe(subscribeRequest);
            log.info("Subscription ARN: {}", subscribeResponse.subscriptionArn());

            // After subscription, send a direct welcome email using SNS
            String message = String.format(
                    "Welcome to BidWise!\n\n" +
                            "Thank you for registering with our auction platform. " +
                            "You will now receive notifications about:\n" +
                            "- New auction listings\n" +
                            "- Bid updates\n" +
                            "- Important announcements\n\n" +
                            "Please confirm your subscription using the link in the previous email to start receiving notifications.\n\n" +
                            "Best regards,\n" +
                            "The BidWise Team"
            );

            // Send welcome message directly to the user's email
            PublishRequest publishRequest = PublishRequest.builder()
                    .topicArn(awsProperties.getWelcomeTopicArn()) // Use a separate topic for welcome messages
                    .message(message)
                    .subject("Welcome to BidWise! 🎉")
                    .build();

            PublishResponse publishResult = snsClient.publish(publishRequest);
            log.info("Welcome message sent directly. MessageId: {}", publishResult.messageId());

        } catch (SnsException e) {
            log.error("Error in SNS operations: ", e);
            throw new RuntimeException("Failed to process SNS operations", e);
        }
    }

    public void sendGeneralNotification(String subject, String message) {
        try {
            PublishRequest publishRequest = PublishRequest.builder()
                    .topicArn(awsProperties.getTopicArn())
                    .subject(subject)
                    .message(message)
                    .build();

            PublishResponse publishResult = snsClient.publish(publishRequest);
            log.info("General notification sent. MessageId: {}", publishResult.messageId());
        } catch (SnsException e) {
            log.error("Error sending general notification: ", e);
            throw new RuntimeException("Failed to send general notification", e);
        }
    }
}