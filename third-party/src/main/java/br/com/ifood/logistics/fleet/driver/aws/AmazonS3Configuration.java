package br.com.company.logistics.project.driver.aws;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.company.event.amazon.config.AmazonConfiguration;
import com.company.event.amazon.utils.AWSUtils;
import com.company.file.AmazonS3ServiceImpl;
import com.company.file.FileRepositoryService;
import com.company.file.amazon.s3.AmazonS3BucketConfiguration;

@Configuration
public class AmazonS3Configuration {

    @Bean("amazonS3BucketConfiguration")
    @ConfigurationProperties(prefix = "amazon.s3.logistics-data")
    public AmazonS3BucketConfiguration amazonS3BucketConfiguration() {
        return new AmazonS3BucketConfiguration();
    }

    @Bean
    public FileRepositoryService amazons3FileRepositoryService(@Qualifier("amazonConfiguration") final AmazonConfiguration amazonConfig,
                                                               @Qualifier("amazonS3BucketConfiguration") final AmazonS3BucketConfiguration bucketConfig,
                                                               @Value("${amazon.s3.local.endpoint:}") final String localEndpoint) {
        final AWSCredentialsProvider provider = AWSUtils.getAWSCredentialsProvider(amazonConfig);
        final String defaultBucket = bucketConfig.getBucketName();
        final String region = amazonConfig.getRegion();
        if (StringUtils.isBlank(localEndpoint)) {
            return new AmazonS3ServiceImpl(provider, region, defaultBucket, null);
        }
        return new AmazonS3ServiceImpl(provider, region, defaultBucket, null, localEndpoint);
    }
}
