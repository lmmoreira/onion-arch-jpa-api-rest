package br.com.company.logistics.project.driver;

import static br.com.company.logistics.project.driver.DriverAttributeName.EMAIL;
import static br.com.company.logistics.project.driver.DriverAttributeName.FULL_NAME;
import static br.com.company.logistics.project.driver.DriverAttributeName.MOTHERS_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class DriverToDriverEntityConverterTest {

    @InjectMocks
    private DriverToDriverEntityConverter converter;
    @Mock
    private DriverEntityRepository driverEntityRepository;

    @Test
    void shouldConvertToDriverEntity() {
        final var attributes =
            Set.of(DriverAttribute.of(FULL_NAME, "Driver da Silva"), DriverAttribute.of(EMAIL, "test@company.com.br"));
        final var driver = Driver.of(UUID.randomUUID(), "45", "BR","project", ZonedDateTime.now(), UUID.randomUUID(), attributes);

        Mockito.when(driverEntityRepository.findDriverAndAttributesBy(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        final DriverEntity entity = converter.convert(driver);

        assertThat(entity).isNotNull();
        assertThat(entity.getUuid()).isEqualTo(driver.getUuid());
        assertThat(entity.getExternalId()).isEqualTo(driver.getExternalId());
        assertThat(entity.getTenant()).isEqualTo(driver.getTenant());
        assertThat(entity.getExternalUpdatedAt()).isEqualTo(driver.getExternalUpdatedAt());
        assertThat(entity.getAttributes()).hasSize(attributes.size());
        attributes.forEach(attribute -> {
            assertThat(entity.getAttributes())
                    .extracting(DriverAttributeEntity::getName, DriverAttributeEntity::getValue)
                    .contains(tuple(attribute.getName(), attribute.getValue().get()));
        });
    }

    @Test
    void shouldConvertToDriverEntityWhenExitsPreSaved() {
        final UUID driverUuid = UUID.randomUUID();
        final UUID userUuid = UUID.randomUUID();
        final UUID savedEmailDiverAttributeUuid = UUID.randomUUID();
        final String name = "Driver da Silva";
        final String motherName = "MOTHER NAME";
        final String email = "test@company.com.br";
        final ZonedDateTime externalUpdatedAt = ZonedDateTime.now();
        final String tenant = "BR";
        final String deliveryExternalSystem = "project";
        final String externalId = "12345";

        final DriverEntity savedEntity =
            DriverEntity.of(driverUuid, tenant, externalUpdatedAt);
        savedEntity.setExternalId(externalId);
        savedEntity.setDeliveryExternalSystem(deliveryExternalSystem);
        savedEntity.setUserUuid(userUuid);
        savedEntity.getAttributes()
                .add(DriverAttributeEntity.of(savedEmailDiverAttributeUuid, EMAIL, "test_old@company.com.br"));
        savedEntity.getAttributes().add(DriverAttributeEntity.of(UUID.randomUUID(), MOTHERS_NAME, motherName));

        Mockito.when(driverEntityRepository.findDriverAndAttributesBy(driverUuid)).thenReturn(Optional.of(savedEntity));

        final var driver = Driver.of(driverUuid,
            externalId,
            tenant,
            deliveryExternalSystem,
            externalUpdatedAt,
            userUuid,
            Set.of(DriverAttribute.of(FULL_NAME, name), DriverAttribute.of(EMAIL, email)));

        final DriverEntity entity = converter.convert(driver);

        assertThat(entity).isNotNull();
        assertThat(entity.getUuid()).isEqualTo(driver.getUuid());
        assertThat(entity.getExternalId()).isEqualTo(driver.getExternalId());
        assertThat(entity.getUserUuid()).isEqualTo(driver.getUserUuid());
        assertThat(entity.getTenant()).isEqualTo(driver.getTenant());
        assertThat(entity.getExternalUpdatedAt()).isEqualTo(driver.getExternalUpdatedAt());
        assertThat(entity.getAttributes()).hasSize(2);
        assertThat(getDriverAttributeEntityBy(entity, FULL_NAME).getValue()).isEqualTo(name);
        assertThat(getDriverAttributeEntityBy(entity, EMAIL).getUuid()).isEqualTo(savedEmailDiverAttributeUuid);
        assertThat(getDriverAttributeEntityBy(entity, EMAIL).getValue()).isEqualTo(email);
        assertThat(getDriverAttributeEntityBy(entity, MOTHERS_NAME)).isNull();
    }

    private DriverAttributeEntity getDriverAttributeEntityBy(final DriverEntity driverEntity,
                                                             final DriverAttributeName name) {
        return driverEntity.getAttributes()
                .stream()
                .filter(attribute -> attribute.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
