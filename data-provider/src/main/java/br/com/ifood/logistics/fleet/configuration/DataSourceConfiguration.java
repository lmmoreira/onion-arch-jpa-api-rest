package br.com.company.logistics.project.configuration;

import static br.com.company.logistics.project.configuration.RoutingDataSource.Route.READ;
import static br.com.company.logistics.project.configuration.RoutingDataSource.Route.WRITE;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.write.datasource.hikari")
    public HikariConfig writeConfiguration() {
        return new HikariConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.read.datasource.hikari")
    public HikariConfig readConfiguration() {
        return new HikariConfig();
    }

    @Bean
    public DataSource writeDataSource() {
        return new HikariDataSource(writeConfiguration());
    }

    @Bean
    public DataSource readDataSource() {
        return new HikariDataSource(readConfiguration());
    }

    @Profile(value = "test")
    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(writeDataSource());
    }

    @DependsOn(value = {"writeDataSource", "readDataSource"})
    @Bean(name = "dataSource")
    @Primary
    DataSource dataSource(@Qualifier("readDataSource") final DataSource readDataSource,
                          @Qualifier("writeDataSource") final DataSource writeDataSource) {
        final var targetDataSources = Map.<Object, Object>of(WRITE, writeDataSource, READ, readDataSource);
        final var routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(writeDataSource);

        return routingDataSource;
    }

    @Primary
    @Bean("entityManagerFactory")
    LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource,
                                                                final JpaProperties jpaProperties) {
        final var entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan("br.com.company.logistics");
        entityManagerFactoryBean.setJpaProperties(hibernateProperties(jpaProperties));
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        return entityManagerFactoryBean;
    }

    private Properties hibernateProperties(final JpaProperties jpaProperties) {
        final var properties = new Properties();
        properties.putAll(jpaProperties.getProperties());

        return properties;
    }

    @Bean("transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(@Qualifier("jpaTxManager") PlatformTransactionManager wrapped) {
        return new ReplicaAwareTransactionManager(wrapped);
    }

    @Bean(name = "jpaTxManager")
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    TransactionTemplate transactionTemplate(@Qualifier("transactionManager") final PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean(name = "readOnlyTransactionTemplate")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    TransactionTemplate readTransactionTemplate(@Qualifier("transactionManager") final PlatformTransactionManager transactionManager) {
        return new ReadTransactionTemplate(transactionManager);
    }
}
