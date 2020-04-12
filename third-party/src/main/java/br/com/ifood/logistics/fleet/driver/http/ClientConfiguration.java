package br.com.company.logistics.project.driver.http;

import br.com.company.logistics.project.driver.IdentityApiClient;
import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    public IdentityApiClient identityApiClient(@Value("${identity.endpoint}") final String endpoint,
                                               @Value("${identity.connectTimeout}") final int connectTimeout,
                                               @Value("${identity.socketTimeout}") final int socketTimeout) {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .client(new ApacheHttpClient())
                .retryer(Retryer.NEVER_RETRY)
                .options(new Request.Options(connectTimeout, socketTimeout))
                .target(IdentityApiClient.class, endpoint);
    }

}
