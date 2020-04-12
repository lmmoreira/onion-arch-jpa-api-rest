package br.com.company.logistics.project.driver;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DriverToDriverResponseConverter implements Converter<Driver, DriverResponse> {

    private final ConfigurableConversionService conversionService;

    @PostConstruct
    void postConstruct() {
        conversionService.addConverter(this);
    }

    public DriverResponse convert(final Driver driver) {
        return new DriverResponse(driver.getUuid(), driver.getExternalId(), driver.getTenant(), driver.getDeliveryExternalSystem(),
            driver.getExternalUpdatedAt(), driver.getUserUuid(), getAttributes(driver));
    }

    private Set<DriverAttributeResponse> getAttributes(final Driver driver) {
        return driver.getAttributes().stream().map(this::convertToDriverAttribute).collect(Collectors.toSet());
    }

    private DriverAttributeResponse convertToDriverAttribute(final DriverAttribute driverAttribute) {
        return new DriverAttributeResponse(driverAttribute.getName().name(), driverAttribute.getValue().orElse(null),
            driverAttribute.getType().name(), driverAttribute.getUpdatedAt());
    }
}
