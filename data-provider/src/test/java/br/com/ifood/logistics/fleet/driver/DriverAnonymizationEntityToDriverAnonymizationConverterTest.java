package br.com.company.logistics.project.driver;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.javafaker.Faker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class DriverAnonymizationEntityToDriverAnonymizationConverterTest {

    @InjectMocks
    private DriverAnonymizationEntityToDriverAnonymizationConverter converter;

    @Test
    public void shouldConvertToDriverAnonymization() {
        final UUID uuid = UUID.randomUUID();
        final UUID driverUuid = UUID.randomUUID();
        final String user = Faker.instance().name().firstName();

        final DriverAnonymizationEntity entity = DriverAnonymizationEntity.of(uuid, driverUuid, user);
        entity.setAnonymizedAt(ZonedDateTime.now());
        final DriverAnonymization driverAnonymization = converter.convert(entity);

        assertThat(driverAnonymization).isNotNull();
        assertThat(driverAnonymization.getUuid()).isEqualTo(entity.getUuid());
        assertThat(driverAnonymization.getUserUuid()).isEqualTo(entity.getUserUuid());
        assertThat(driverAnonymization.getDriverUuid()).isEqualTo(entity.getDriverUuid());
        assertThat(driverAnonymization.getAnonymizedAt()).isEqualTo(entity.getAnonymizedAt());
    }
}
