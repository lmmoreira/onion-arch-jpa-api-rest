package br.com.company.logistics.project.configuration;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.sql.DataSource;

public final class H2ResetExtension implements BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    private static final String H2_BACKUP_PATH = "./h2/h2backup.sql";
    private final JdbcTemplate jdbcTemplate;

    public H2ResetExtension() {
        DataSource dataSource = DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:company-project-driver-account")
                .username("company-project-driver-account-app")
                .password("company-project-driver-account-app")
                .build();
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate.execute(createSchema());
    }

    private static final String createSchema() {
        final StringBuilder query = new StringBuilder();
        query.append("CREATE SCHEMA IF NOT EXISTS \"company-project-DRIVER-ACCOUNT\";");
        query.append("CREATE ROLE IF NOT EXISTS \"company-project-DRIVER-ACCOUNT\";");
        query.append("grant all on schema \"company-project-DRIVER-ACCOUNT\" to \"company-project-driver-account\";");
        query.append("grant all on schema \"company-project-DRIVER-ACCOUNT\" to \"company-project-driver-account-app\";");
        return query.toString();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        jdbcTemplate.execute("DROP ALL OBJECTS");
        jdbcTemplate.execute(String.format("RUNSCRIPT FROM '%s'", H2_BACKUP_PATH));
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        jdbcTemplate.execute(String.format("SCRIPT TO '%s'", H2_BACKUP_PATH));
    }

    @Override
    public void afterAll(ExtensionContext context) {
        try {
            if (Files.exists(Paths.get(H2_BACKUP_PATH))) {
                Files.delete(Paths.get(H2_BACKUP_PATH));
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
