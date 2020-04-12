package br.com.company.logistics.project.driver;

import static br.com.company.logistics.project.driver.DriverAttributeName.valueOf;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.EnumUtils.isValidEnum;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

import com.newrelic.api.agent.NewRelic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import br.com.company.logistics.project.common.DeliveryExternalSystemResolver;
import br.com.company.logistics.project.common.TenantIdentifierHolder;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DriverRequestToDriverConverter implements Converter<DriverRequest, Driver> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverRequestToDriverConverter.class);
    
    private final TenantIdentifierHolder tenantIdentifierHolder;
    private final DeliveryExternalSystemResolver deliveryExternalSystemResolver;
    private final ConfigurableConversionService conversionService;
    
    @PostConstruct
    void postConstruct() {
        conversionService.addConverter(this);
    }

    @Override
    public Driver convert(final DriverRequest driverRequest) {
        final String tenant = tenantIdentifierHolder.getValue(driverRequest.getTenant());
        return Driver.of(firstNonNull(driverRequest.getUuid(), UUID.randomUUID()),
            driverRequest.getExternalId(),
            tenant,
            deliveryExternalSystemResolver.formatDeliveryExternalSystem(tenant,
                driverRequest.getDeliveryExternalSystem()),
            driverRequest.getExternalUpdatedAt(),
            driverRequest.getUserUuid(),
            getAttributes(driverRequest));
    }

    private Set<DriverAttribute> getAttributes(final DriverRequest driverRequest) {
        return driverRequest.getAttributes()
                .stream()
                .map(this::convertToDriverAttribute)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    private Optional<DriverAttribute> convertToDriverAttribute(final DriverAttributeRequest driverAttributeRequest) {
        final Optional<DriverAttribute> driverAttribute = Optional.ofNullable(driverAttributeRequest.getName())
                .filter(this::isValidEnumDriverAttributeName)
                .map(createDriverAttribute(driverAttributeRequest));
        if (driverAttribute.isEmpty()) {
            reportUnknownAttribute(driverAttributeRequest.getName());
        }
        return driverAttribute;
    }

    private Function<String, DriverAttribute> createDriverAttribute(final DriverAttributeRequest driverAttributeRequest) {
        return name -> DriverAttribute.of(valueOf(name), driverAttributeRequest.getValue());
    }

    private boolean isValidEnumDriverAttributeName(final String name) {
        return isValidEnum(DriverAttributeName.class, name);
    }

    private void reportUnknownAttribute(final String name) {
        final String message = String.format("Attribute not found: %s ", name);
        LOGGER.warn(message);
        NewRelic.noticeError(message, true);
    }
}
