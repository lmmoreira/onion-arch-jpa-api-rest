package br.com.company.logistics.project.driver.aws;

import com.deliverypf.lock.LockService;
import com.company.event.AbstractMessageHandler;
import com.company.event.AbstractQueueConsumer;
import com.company.event.AbstractQueueSender;
import com.company.event.amazon.config.AmazonConfiguration;
import com.company.event.amazon.sqs.AmazonSQSPreMessageHandling;
import com.company.event.amazon.sqs.AmazonSQSQueueConsumerBuilder;
import com.company.event.amazon.sqs.config.AmazonSQSQueueWorkerConfiguration;
import com.company.event.config.QueueWorkerRetryConfiguration;
import com.company.event.message.TransactionalMessage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.support.RetryTemplate;

import br.com.company.logistics.project.driver.handler.DriverAccountCreateOrUpdateMessage;

@Configuration
public class AmazonSqsConsumerConfiguration {

    @Bean(name = "sqsRetryOperations")
    public RetryOperations sqsRetryOperations() {
        return new RetryTemplate();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public AbstractQueueConsumer driverAccountCreateOrUpdateConsumer(@Qualifier("amazonConfiguration") final AmazonConfiguration awsConfig,
                                                                     @Qualifier("driverAccountCreateOrUpdateHandler") final AbstractMessageHandler<String, DriverAccountCreateOrUpdateMessage> messageHandler,
                                                                     @Qualifier("driverAccountCreateOrUpdateConfig") final AmazonSQSQueueWorkerConfiguration handlerConfig,
                                                                     @Qualifier("queueSender") final AbstractQueueSender retrySender,
                                                                     @Qualifier("sqsLockService") final LockService lockService,
                                                                     @Qualifier("sqsRetryOperations") final RetryOperations retryOperations) {
        return newAmazonSQSQueueConsumer(awsConfig,
            messageHandler,
            handlerConfig,
            retrySender,
            lockService,
            retryOperations);
    }

    @Bean(name = "driverAccountCreateOrUpdateConfig")
    @ConfigurationProperties(prefix = "handler.driver-create-or-update")
    public AmazonSQSQueueWorkerConfiguration driverAccountCreateOrUpdateConfig() {
        return new AmazonSQSQueueWorkerConfiguration();
    }

    private <T> AbstractQueueConsumer newAmazonSQSQueueConsumer(final AmazonConfiguration amazonConfiguration,
                                                                final AbstractMessageHandler<String, T> messageHandler,
                                                                final AmazonSQSQueueWorkerConfiguration handlerConfiguration,
                                                                final AbstractQueueSender retrySender,
                                                                final LockService lockService,
                                                                final RetryOperations retryOperations) {


        return newAmazonSQSQueueConsumer(amazonConfiguration,
            messageHandler,
            handlerConfiguration,
            retrySender,
            lockService,
            retryOperations,
            handlerConfiguration.getQueue() + "_DLQ");
    }

    private <T> AbstractQueueConsumer newAmazonSQSQueueConsumer(final AmazonConfiguration amazonConfiguration,
                                                                final AbstractMessageHandler<String, T> messageHandler,
                                                                final AmazonSQSQueueWorkerConfiguration handlerConfiguration,
                                                                final AbstractQueueSender retrySender,
                                                                final LockService lockService,
                                                                final RetryOperations retryOperations,
                                                                final String dlqQueueName) {

        final AmazonSQSPreMessageHandling preHandler = TransactionalMessage::getTransactionId;

        return newAmazonSQSQueueConsumer(amazonConfiguration,
            messageHandler,
            handlerConfiguration,
            retrySender,
            lockService,
            retryOperations,
            dlqQueueName,
            preHandler);
    }

    private <T> AbstractQueueConsumer newAmazonSQSQueueConsumer(final AmazonConfiguration amazonConfiguration,
                                                                final AbstractMessageHandler<String, T> messageHandler,
                                                                final AmazonSQSQueueWorkerConfiguration handlerConfiguration,
                                                                final AbstractQueueSender retrySender,
                                                                final LockService lockService,
                                                                final RetryOperations retryOperations,
                                                                final String dlqQueueName,
                                                                final AmazonSQSPreMessageHandling preHandler) {
        final QueueWorkerRetryConfiguration retryConfiguration = new QueueWorkerRetryConfiguration();
        retryConfiguration.setRetrySender(retrySender);
        retryConfiguration.setDlqQueue(dlqQueueName);
        final AmazonSQSQueueConsumerBuilder amazonSQSQueueConsumerBuilder = AmazonSQSQueueConsumerBuilder.get()
                .withAmazonConfiguration(amazonConfiguration)
                .withAmazonSQSWorkerConfiguration(handlerConfiguration)
                .withQueueWorkerRetryConfiguration(retryConfiguration)
                .withMessageHandler(messageHandler)
                .withRetryOperations(retryOperations)
                .withLockService(lockService);
        if (preHandler != null) {
            amazonSQSQueueConsumerBuilder.withMessagePreHandler(preHandler)
                    .withMessagePostHandler(TransactionalMessage::getTransactionId);
        }
        return amazonSQSQueueConsumerBuilder.build();
    }

}
