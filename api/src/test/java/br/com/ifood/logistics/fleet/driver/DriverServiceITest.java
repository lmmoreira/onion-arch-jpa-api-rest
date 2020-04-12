package br.com.company.logistics.project.driver;

import static br.com.company.logistics.project.driver.DriverAttributeName.FATHERS_NAME;
import static br.com.company.logistics.project.driver.DriverAttributeName.FULL_NAME;
import static br.com.company.logistics.project.driver.DriverAttributeName.MOTHERS_NAME;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import br.com.company.logistics.project.driver.integration.AbstractIntegrationTest;

public class DriverServiceITest extends AbstractIntegrationTest {

    private final Faker faker = new Faker();
    
    @Autowired
    private DriverService driverService;

    @Test
    void shouldSaveNewDriver() {
        final Driver driver = driverService.save(createDriver());
        final Driver driverSaved = driverService.findDriverAndAttributesBy(driver.getUuid(), List.of()).orElse(null);
        assertThat(driverSaved).isEqualTo(driver);
    }

    @Test
    void shouldUpdateDriver() {
        final Driver driver = driverService.save(createDriver());
        final Set<DriverAttribute> attributes = new HashSet<>();
        attributes.addAll(driver.getAttributes());
        attributes.addAll(Set.of(DriverAttribute.of(FULL_NAME, faker.name().fullName()),
            DriverAttribute.of(MOTHERS_NAME, faker.name().fullName())));
        final Driver driverUpdated = Driver.of(driver.getUuid(),
            driver.getExternalId(),
            driver.getTenant(),
            driver.getDeliveryExternalSystem(),
            driver.getExternalUpdatedAt().plusDays(1L),
            driver.getUserUuid(),
            attributes);
        driverService.save(driverUpdated);

        final Driver driverSaved = driverService.findDriverAndAttributesBy(driver.getUuid(), List.of()).orElse(null);
        assertThat(driverUpdated.getUuid()).isEqualTo(driverSaved.getUuid());
        assertThat(driverUpdated.getTenant()).isEqualTo(driverSaved.getTenant());
        assertThat(driverUpdated.getExternalId()).isEqualTo(driverSaved.getExternalId());
        assertThat(driverUpdated.getDeliveryExternalSystem()).isEqualTo(driverSaved.getDeliveryExternalSystem());
        assertThat(driverUpdated.getAttributes()).isEqualTo(driverSaved.getAttributes());
    }

    private Driver createDriver() {
        return Driver.of(randomUUID(),
            Objects.toString(ThreadLocalRandom.current().nextLong()),
            "br",
            "project",
            ZonedDateTime.now(),
            randomUUID(),
            Set.of(DriverAttribute.of(FULL_NAME, faker.name().fullName()),
                DriverAttribute.of(FATHERS_NAME, faker.name().fullName()),
                DriverAttribute.of(DriverAttributeName.DRIVERS_LICENSE_PHOTO,
                    "http://teste.com.br/driver_license.jpg")));
    }

    @Test
    void shouldSaveDriverAttribute() {
        final Driver driver = createDriver();
        driverService.save(driver);
        final DriverAttribute driverAttribute =
            DriverAttribute.of(DriverAttributeName.WORKER_PHOTO, "http://teste.com.br/foto_worker.jpg");

        driverService.saveDriverAttribute(driver.getUuid(), driverAttribute.getName(), driverAttribute.getValue().get());

        final Driver driverSaved = driverService.findDriverAndAttributesBy(driver.getUuid(), List.of()).orElse(null);
        assertNotNull(driverSaved);
        assertThat(driverSaved.getAttributes()).extracting(DriverAttribute::getName, DriverAttribute::getValue)
                .contains(tuple(driverAttribute.getName(), driverAttribute.getValue()));
    }

    @Test
    void shouldUpdateDriverAttribute() {
        final Driver driver = Driver.of(randomUUID(),
            Objects.toString(ThreadLocalRandom.current().nextLong()),
            "br",
            "project",
            ZonedDateTime.now(),
            randomUUID(),
            Set.of(DriverAttribute.of(DriverAttributeName.DRIVERS_LICENSE_PHOTO,
                "http://teste.com.br/driver_license.jpg")));
        driverService.save(driver);
        final DriverAttribute driverAttribute =
            DriverAttribute.of(DriverAttributeName.DRIVERS_LICENSE_PHOTO, "http://teste.com.br/driver_foto.jpg");

        driverService
                .saveDriverAttribute(driver.getUuid(), driverAttribute.getName(), driverAttribute.getValue().get());

        final Driver driverSaved = driverService.findDriverAndAttributesBy(driver.getUuid(), List.of()).orElse(null);
        assertNotNull(driverSaved);
        assertThat(driverSaved.getAttributes()).extracting(DriverAttribute::getName, DriverAttribute::getValue)
                .contains(tuple(driverAttribute.getName(), driverAttribute.getValue()));
    }
}
