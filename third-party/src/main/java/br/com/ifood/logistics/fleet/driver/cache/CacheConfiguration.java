package br.com.company.logistics.project.driver.cache;

import com.deliverypf.cache.CacheService;
import com.deliverypf.cache.LettuceCacheService;
import com.deliverypf.cache.LettuceConnection;
import com.deliverypf.lock.CustomRedisLockProvider;
import com.deliverypf.lock.LockService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class CacheConfiguration {

    @Value("${cache.redis.url}")
    private String url;

    @Value("${cache.redis.read-timeout}")
    private Integer readTimeout;

    @Value("${cache.redis.reconnection-delay}")
    private Integer reconnectionDelay;

    @Bean
    @Primary
    public LettuceConnection lettuceConnection() {
        return new LettuceConnection(url, readTimeout, reconnectionDelay);
    }

    @Bean("cacheService")
    @Primary
    public CacheService cacheService() {
        return new LettuceCacheService(lettuceConnection());
    }

    @Bean(name = "sqsLockService")
    public LockService getLockService(@Value("${cache.manager.lock-time}") final Long lockTime) {
        return new CustomRedisLockProvider(cacheService(), lockTime);
    }

}
