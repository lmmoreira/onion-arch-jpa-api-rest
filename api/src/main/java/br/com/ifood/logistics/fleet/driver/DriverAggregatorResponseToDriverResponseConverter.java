package br.com.company.logistics.project.driver;

import static br.com.company.logistics.project.driver.DriverAttributeType.FILE_NAME;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DriverAggregatorResponseToDriverResponseConverter
        implements Converter<DriverAggregatorResponse, DriverResponse> {

    private static final String DELIVER_EXTERNAL_SYSTEM_project = "project";

    private final ConfigurableConversionService conversionService;

    private final DriverFileService driverFileService;

    @PostConstruct
    void postConstruct() {
        conversionService.addConverter(this);
    }

    public DriverResponse convert(final DriverAggregatorResponse driverAggregatorResponse) {
        final Driver driver = driverAggregatorResponse.getDriver();
        return new DriverResponse(driver.getUuid(), driver.getExternalId(), driver.getTenant(),
            driver.getDeliveryExternalSystem(), driver.getExternalUpdatedAt(), driver.getUserUuid(),
            getAttributes(driverAggregatorResponse));
    }

    private Set<DriverAttributeResponse> getAttributes(final DriverAggregatorResponse driverAggregatorResponse) {
        final Driver driver = driverAggregatorResponse.getDriver();
        final DriverAttributeType strategy =
            chooseStrategy(driverAggregatorResponse.getStrategy(), driver.getDeliveryExternalSystem());
        final DriverAttributeCommand command =
            DriverAttributeLinkStrategy.of(driver.getUuid(), driverFileService).toStrategy(strategy);
        return driver.getAttributes()
                .stream()
                .map(command::execute)
                .map(this::convertToDriverAttribute)
                .collect(Collectors.toSet());
    }

    private DriverAttributeResponse convertToDriverAttribute(final DriverAttribute driverAttribute) {
        return new DriverAttributeResponse(driverAttribute.getName().name(), driverAttribute.getValue().orElse(null),
            driverAttribute.getType().name(), driverAttribute.getUpdatedAt());
    }

    private DriverAttributeType chooseStrategy(final DriverAttributeType strategyAttributesSelection,
                                               final String deliverExternalSystem) {
        return isDeliveryExternalSystemproject(deliverExternalSystem) ? strategyAttributesSelection : FILE_NAME;
    }

    private boolean isDeliveryExternalSystemproject(final String deliverExternalSystem) {
        return StringUtils.startsWith(deliverExternalSystem, DELIVER_EXTERNAL_SYSTEM_project);
    }
}
