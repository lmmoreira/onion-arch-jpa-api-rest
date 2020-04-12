package br.com.company.logistics.project.driver.aws;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.support.RetryTemplate;

import com.company.event.AbstractQueueSender;
import com.company.event.amazon.config.AmazonConfiguration;
import com.company.event.amazon.sqs.AmazonSQSPrePublishingMessageProcessor;
import com.company.event.amazon.sqs.AmazonSQSQueueSender;

@Configuration
public class AmazonBaseConfiguration {

    @Bean("amazonConfiguration")
    @ConfigurationProperties(prefix = "amazon")
    public AmazonConfiguration newAmazonConfiguration() {
        return new AmazonConfiguration();
    }

    @Bean(name = "sqsSenderRetryOperations")
    public RetryOperations sqsSenderRetryOperations() {
        return new RetryTemplate();
    }

    @Bean("queueSender")
    public AbstractQueueSender newMessageQueueSender(@Qualifier("amazonConfiguration") final AmazonConfiguration amazonConfiguration,
                                                     @Qualifier("sqsSenderRetryOperations") final RetryOperations retryOperations,
                                                     @Qualifier("amazonSQSPrePublishingMessageProcessor") final AmazonSQSPrePublishingMessageProcessor preProcessor) {
        return AmazonSQSQueueSender.of(amazonConfiguration, retryOperations, preProcessor);
    }

    @Bean
    public AmazonSQSPrePublishingMessageProcessor amazonSQSPrePublishingMessageProcessor() {
        return AmazonSQSPrePublishingMessageProcessor.nopProcessor.INSTANCE;
    }

}
