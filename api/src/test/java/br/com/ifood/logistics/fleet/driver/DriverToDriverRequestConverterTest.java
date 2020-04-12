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
class DriverToDriverResponseConverterTest {

    private static final String DELIVERY_EXTERNAL_SYSTEM_project_CO = "project-co";
    
    private final Faker faker = new Faker();

    @InjectMocks
    private DriverRequestToDriverConverter requestConverter;
    
    @InjectMocks
    private DriverToDriverResponseConverter responseConverter;
    
    @Spy
    private DeliveryExternalSystemResolver deliveryExternalSystemResolver;

    @Mock
    private TenantIdentifierHolder tenantIdentifierHolder;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldConvertToDriverRequest() {
        final var attributes = Set.of(new DriverAttributeRequest(FULL_NAME.name(), faker.name().fullName()),
            new DriverAttributeRequest(EMAIL.name(), faker.internet().emailAddress()));
        final var driver = new DriverRequest(UUID.randomUUID(), "985447", "co", "project", ZonedDateTime.now(), UUID.randomUUID(), attributes);
        when(tenantIdentifierHolder.getValue(any())).thenReturn("co");
        
        final DriverResponse driverResponse = responseConverter.convert(requestConverter.convert(driver));

        assertNotNull(driverResponse);
        assertEquals(driver.getUuid(), driverResponse.getUuid());
        assertEquals(driver.getExternalId(), driverResponse.getExternalId());
        assertEquals(driver.getUserUuid(), driverResponse.getUserUuid());
        assertEquals(driver.getTenant(), driverResponse.getTenant());
        assertEquals(DELIVERY_EXTERNAL_SYSTEM_project_CO, driverResponse.getDeliveryExternalSystem());
        assertEquals(driver.getExternalUpdatedAt(), driverResponse.getExternalUpdatedAt());
        assertThat(driverResponse.getAttributes()).hasSize(attributes.size());
        driverResponse.getAttributes().forEach(attribute -> {
            assertThat(driver.getAttributes())
                    .extracting(DriverAttributeRequest::getName, DriverAttributeRequest::getValue)
                    .contains(tuple((attribute.getName()), attribute.getValue()));
        });
    }
}
