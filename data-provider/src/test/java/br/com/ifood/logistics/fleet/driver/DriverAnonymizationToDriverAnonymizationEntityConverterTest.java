package br.com.company.logistics.project.driver;

import static br.com.company.logistics.project.driver.DriverAttributeName.EMAIL;
import static br.com.company.logistics.project.driver.DriverAttributeName.FULL_NAME;
import static br.com.company.logistics.project.driver.DriverAttributeName.MOTHERS_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.github.javafaker.Faker;
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
class DriverAnonymizationToDriverAnonymizationEntityConverterTest {

    @InjectMocks
    private DriverAnonymizationToDriverAnonymizationEntityConverter converter;

    @Test
    void shouldConvertToDriverAnonymizationEntity() {
        final UUID uuid = UUID.randomUUID();
        final UUID driverUuid = UUID.randomUUID();
        final String user = Faker.instance().name().firstName();

        final var driverAnonymization = DriverAnonymization.of(uuid, driverUuid, user);
        final DriverAnonymizationEntity entity = converter.convert(driverAnonymization);

        assertThat(entity).isNotNull();
        assertThat(entity.getUuid()).isEqualTo(driverAnonymization.getUuid());
        assertThat(entity.getUserUuid()).isEqualTo(driverAnonymization.getUserUuid());
        assertThat(entity.getDriverUuid()).isEqualTo(driverAnonymization.getDriverUuid());
    }

}
