package br.com.company.logistics.project.driver.integration;

import com.deliverypf.cache.LettuceConnection;
import com.company.file.FileRepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import br.com.company.logistics.project.configuration.EmbeddedRedisServer;
import br.com.company.logistics.project.configuration.H2ResetExtension;
import br.com.company.logistics.project.configuration.TestContextConfiguration;
import br.com.company.logistics.project.driver.IdentityApiClient;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TestContextConfiguration.class)
@ExtendWith(H2ResetExtension.class)
@EnableJpaRepositories("br.com.company.logistics")
@EntityScan("br.com.company.logistics")
public abstract class AbstractIntegrationTest {

    @MockBean
    protected IdentityApiClient identityApiClient;

    @MockBean
    protected FileRepositoryService fileRepositoryService;

    @Autowired
    private LettuceConnection lettuceConnection;

    @Autowired
    private EmbeddedRedisServer embeddedRedisServer;

    @BeforeEach
    public void clearCache() {
        lettuceConnection.getRedisCommands().flushall();
    }

}
