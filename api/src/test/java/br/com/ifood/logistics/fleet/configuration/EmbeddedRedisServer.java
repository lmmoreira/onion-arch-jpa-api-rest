package br.com.company.logistics.project.configuration;

import static java.util.Objects.isNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestComponent;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import redis.embedded.RedisServer;

@TestComponent
public final class EmbeddedRedisServer {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedRedisServer.class);
    private static RedisServer redisServer;
    @Value("${cache.redis.port:6379}")
    private int redisPort;

    @PostConstruct
    public void setUp() throws IOException {
        log.info("Initializing Embedded Redis on port {}", redisPort);
        if (isNull(redisServer)) {
            redisServer = new RedisServer(redisPort);
            redisServer.start();
        }
        log.info("Embedded Redis initialized on port {}", redisPort);
    }

    @PreDestroy
    public void tearDown() {
        redisServer.stop();
    }
}
