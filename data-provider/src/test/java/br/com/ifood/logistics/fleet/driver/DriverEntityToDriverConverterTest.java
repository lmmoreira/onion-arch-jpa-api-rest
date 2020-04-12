package br.com.company.logistics.project.driver;

import static br.com.company.logistics.project.driver.DriverAttributeName.EMAIL;
import static br.com.company.logistics.project.driver.DriverAttributeName.FULL_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class DriverEntityToDriverConverterTest {

    @InjectMocks
    private DriverEntityToDriverConverter converter;

    @Test
    public void shouldConvertToDriver() {
        final UUID driverUuid = UUID.randomUUID();
        final UUID userUuid = UUID.randomUUID();
        final DriverEntity entity = DriverEntity.of(driverUuid, "BR", ZonedDateTime.now());
        entity.setExternalId("45");
        entity.setDeliveryExternalSystem("project");
        entity.setUserUuid(userUuid);
        entity.getAttributes().add(DriverAttributeEntity.of(UUID.randomUUID(), FULL_NAME, "Driver da Silva"));
        entity.getAttributes().add(DriverAttributeEntity.of(UUID.randomUUID(), EMAIL, "test@company.com.br"));

        final Driver driver = converter.convert(entity);

        assertThat(driver).isNotNull();
        assertThat(driver.getUuid()).isEqualTo(entity.getUuid());
        assertThat(driver.getExternalId()).isEqualTo(entity.getExternalId());
        assertThat(driver.getUserUuid()).isEqualTo(entity.getUserUuid());
        assertThat(driver.getTenant()).isEqualTo(entity.getTenant());
        assertThat(driver.getExternalUpdatedAt()).isEqualTo(entity.getExternalUpdatedAt());
        assertThat(driver.getAttributes()).hasSize(2);
        entity.getAttributes().forEach(attribute -> {
            assertThat(driver.getAttributes()).extracting(DriverAttribute::getName, DriverAttribute::getValue)
                    .contains(tuple(attribute.getName(), Optional.of(attribute.getValue())));
        });
    }
}
