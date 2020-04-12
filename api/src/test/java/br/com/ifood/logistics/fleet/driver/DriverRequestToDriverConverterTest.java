package br.com.company.logistics.project.driver;

import br.com.company.logistics.project.common.DeliveryExternalSystemResolver;
import br.com.company.logistics.project.common.TenantIdentifierHolder;

import static br.com.company.logistics.project.driver.DriverAttributeName.EMAIL;
import static br.com.company.logistics.project.driver.DriverAttributeName.FULL_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class DriverRequestToDriverConverterTest {

    private final Faker faker = new Faker();

    @InjectMocks
    private DriverRequestToDriverConverter converter;

    @Spy
    private DeliveryExternalSystemResolver deliveryExternalSystemResolver;

    @Mock
    private TenantIdentifierHolder tenantIdentifierHolder;

    @Before 
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldConvertToDriver() {
        final UUID driverUuid = UUID.randomUUID();
        final UUID userUuid = UUID.randomUUID();
        final var attributes = Set.of(new DriverAttributeRequest(FULL_NAME.name(), faker.name().fullName()),
            new DriverAttributeRequest(EMAIL.name(), faker.internet().emailAddress()));
        final var driverRequest =
            new DriverRequest(driverUuid, "45784", "br", "", ZonedDateTime.now(), userUuid, attributes);
        when(tenantIdentifierHolder.getValue(any())).thenReturn("br");
        
        final Driver driver = converter.convert(driverRequest);

        assertNotNull(driver);
        assertEquals(driverRequest.getUuid(), driver.getUuid());
        assertEquals(driverRequest.getUserUuid(), driver.getUserUuid());
        assertEquals(driverRequest.getTenant(), driver.getTenant());
        assertEquals(driverRequest.getExternalId(), driver.getExternalId());
        assertEquals(driverRequest.getExternalUpdatedAt(), driver.getExternalUpdatedAt());
        assertEquals(driverRequest.getDeliveryExternalSystem(), driver.getDeliveryExternalSystem());
        assertThat(driver.getAttributes()).hasSize(attributes.size());
        driver.getAttributes().forEach(attribute -> {
            assertEquals(
                driver.getAttributes()
                        .stream()
                        .filter(driverAttributeRequest -> driverAttributeRequest.getName().equals(attribute.getName()))
                        .findFirst()
                        .get()
                        .getValue(),
                attribute.getValue());
        });
    }

    @Test
    public void shouldConvertToDriverWithKnownAttributeOnly() {
        final var driverUuid = UUID.randomUUID();
        final var userUuid = UUID.randomUUID();
        final var driverAttributeRequestUnknown = new DriverAttributeRequest("ALTURA", "1,56");
        final var attributes = Set.of(driverAttributeRequestUnknown,
            new DriverAttributeRequest(FULL_NAME.name(), faker.name().fullName()),
            new DriverAttributeRequest(EMAIL.name(), faker.internet().emailAddress()));
        final var driverRequest =
            new DriverRequest(driverUuid, "45784", "mx", "project", ZonedDateTime.now(), userUuid, attributes);
        when(tenantIdentifierHolder.getValue(any())).thenReturn("mx");
        
        final Driver driver = converter.convert(driverRequest);

        assertNotNull(driver);
        assertEquals(driverRequest.getUuid(), driver.getUuid());
        assertEquals(driverRequest.getExternalId(), driver.getExternalId());
        assertEquals(driverRequest.getUserUuid(), driver.getUserUuid());
        assertEquals(driverRequest.getTenant(), driver.getTenant());
        assertEquals(driverRequest.getExternalUpdatedAt(), driver.getExternalUpdatedAt());
        assertThat(driver.getAttributes()).hasSize(2)
                .extracting(DriverAttribute::getName, DriverAttribute::getValue)
                .doesNotContain(
                    tuple(driverAttributeRequestUnknown.getName(), driverAttributeRequestUnknown.getValue()));
    }
}
