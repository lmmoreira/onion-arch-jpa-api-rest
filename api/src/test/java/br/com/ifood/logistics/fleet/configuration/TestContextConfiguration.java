package br.com.company.logistics.project.configuration;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.com.company.logistics.project.Application;
import br.com.company.logistics.project.driver.aws.AmazonS3Configuration;
import br.com.company.logistics.project.driver.aws.AmazonSqsConsumerConfiguration;

@Configuration
@Profile("test")
@EnableAutoConfiguration(exclude = RedisAutoConfiguration.class)
@ComponentScan(basePackages = "br.com.company.logistics.*",
               excludeFilters = @Filter(type = ASSIGNABLE_TYPE,
                                        classes = {AmazonSqsConsumerConfiguration.class, AmazonS3Configuration.class,
                                            Application.class}))
public class TestContextConfiguration {
    static {
        System.setProperty("h2.serializeJavaObject", "false");
    }
}
