package br.com.company.logistics.project.driver;

import javax.annotation.PostConstruct;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DriverAttributeToDriverAttributeResponseConverter
        implements Converter<DriverAttribute, DriverAttributeResponse> {

    private final ConfigurableConversionService conversionService;

    @PostConstruct
    void postConstruct() {
        conversionService.addConverter(this);
    }

    @Override
    public DriverAttributeResponse convert(final DriverAttribute driverAttribute) {
        return new DriverAttributeResponse(driverAttribute.getName().name(), driverAttribute.getValue().orElse(null),
            driverAttribute.getType().name(), driverAttribute.getUpdatedAt());
    }
}
