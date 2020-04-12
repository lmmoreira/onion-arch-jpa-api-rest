package br.com.company.logistics.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "br.com.company.logistics.*")
@EnableJpaRepositories("br.com.company.logistics")
@EntityScan("br.com.company.logistics")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
