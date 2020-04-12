package br.com.company.logistics.project.driver.handler;

import static br.com.company.logistics.project.driver.DriverAttributeName.EMAIL;
import static br.com.company.logistics.project.driver.DriverAttributeName.FULL_NAME;
import static br.com.company.logistics.project.driver.DriverAttributeName.PHONE;
import static br.com.company.logistics.project.driver.DriverAttributeName.WORKER_PHOTO;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.company.logistics.project.driver.Driver;
import br.com.company.logistics.project.driver.DriverAttribute;
import br.com.company.logistics.project.driver.DriverAttributeName;
import br.com.company.logistics.project.driver.DriverFactory;
import br.com.company.logistics.project.driver.DriverService;
import br.com.company.logistics.project.driver.handler.DriverAccountCreateOrUpdateMessage.Attribute;
import br.com.company.logistics.project.driver.integration.AbstractIntegrationTest;
import lombok.SneakyThrows;

public class DriverAccountCreateOrUpdateHandlerITest extends AbstractIntegrationTest {

    private static final Faker FAKER = new Faker();

    @Autowired
    private DriverAccountCreateOrUpdateHandler handler;
    
    @Autowired
    private ObjectMapper mapper;
    
    @Autowired
    private DriverService driverService;

    @Autowired
    private DriverFactory driverFactory;

    @SneakyThrows
    @Test
    void shouldCreateNewDriver() {
        final DriverAccountCreateOrUpdateMessage message = createDriverAccountCreateOrUpdateMessage(UUID.randomUUID());
        final String rawMessage = mapper.writeValueAsString(message);

        handler.handle(rawMessage);

        final Driver actualDriver = driverService.findDriverAndAttributesBy(message.getUuid())
                .orElseThrow(() -> new AssertionError("No Driver found"));

        assertThat(actualDriver.getExternalId()).isEqualTo(message.getExternalId());
        assertThat(actualDriver.getTenant()).isEqualTo(message.getTenant());
        assertThat(actualDriver.getExternalUpdatedAt()).isEqualTo(message.getExternalUpdatedAt());
        assertThat(actualDriver.getAnonymizedAt()).isNull();
        assertThat(actualDriver.getUserUuid()).isNull();
        assertThat(actualDriver.getAttributes())
                .extracting(DriverAttribute::getName, DriverAttribute::getValue)
                .containsExactlyInAnyOrder(tuples(message.getAttributes()));
    }

    @SneakyThrows
    @Test
    void shouldUpdateDriverAttributePhoto() {
        final Driver driver = driverFactory.createDriver();
        final DriverAccountCreateOrUpdateMessage message = createDriverAccountCreateOrUpdateMessage(driver.getUuid());
        final String rawMessage = mapper.writeValueAsString(message);

        handler.handle(rawMessage);

        final Driver actualDriver = driverService.findDriverAndAttributesBy(message.getUuid())
                .orElseThrow(() -> new AssertionError("No Driver found"));

        final Set<Attribute>
                workPhotoAttributeOnly =
                message.getAttributes()
                        .stream()
                        .filter(a -> WORKER_PHOTO.name().equals(a.getName()))
                        .collect(Collectors.toSet());
        assertThat(actualDriver.getAttributes())
                .extracting(DriverAttribute::getName, DriverAttribute::getValue)
                .containsExactlyInAnyOrder(tuples(workPhotoAttributeOnly, driver.getAttributes()));
    }

    private Tuple[] tuples(final Set<Attribute> newAttributes, final Set<DriverAttribute> oldAttributes) {
        final Map<DriverAttributeName, String> tupleMap = oldAttributes.stream()
                .collect(Collectors.toMap(DriverAttribute::getName, a -> a.getValue().get()));
        newAttributes.forEach(a -> tupleMap.put(DriverAttributeName.valueOf(a.getName()), a.getValue()));
        return tupleMap.entrySet().stream()
                .map(e -> Tuple.tuple(e.getKey(), Optional.of(e.getValue())))
                .toArray(Tuple[]::new);
    }

    private Tuple[] tuples(final Set<Attribute> attributes) {
        return attributes.stream()
                .map(a -> Tuple.tuple(DriverAttributeName.valueOf(a.getName()), Optional.of(a.getValue())))
                .toArray(Tuple[]::new);
    }

    private DriverAccountCreateOrUpdateMessage createDriverAccountCreateOrUpdateMessage(final UUID uuid) {
        return new DriverAccountCreateOrUpdateMessage(
            uuid,
            FAKER.number().digits(5),
            "br",
            "project",
            ZonedDateTime.now(),
            Set.of(
                new Attribute(FULL_NAME.name(), FAKER.name().fullName()),
                new Attribute(EMAIL.name(), FAKER.internet().emailAddress()),
                new Attribute(PHONE.name(), FAKER.phoneNumber().phoneNumber()),
                new Attribute(WORKER_PHOTO.name(), FAKER.file().fileName())
            )
        );
    }
}
