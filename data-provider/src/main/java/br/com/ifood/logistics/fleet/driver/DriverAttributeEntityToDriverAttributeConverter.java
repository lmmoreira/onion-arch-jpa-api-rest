package br.com.company.logistics.project.driver;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DriverAttributeEntityToDriverAttributeConverter
        implements Converter<DriverAttributeEntity, DriverAttribute> {
    
    private final ConfigurableConversionService conversionService;

    @PostConstruct
    void postConstruct() {
        conversionService.addConverter(this);
    }

    @Override
    public DriverAttribute convert(final DriverAttributeEntity driverAttributeEntity) {
        return DriverAttribute.of(driverAttributeEntity.getName(), driverAttributeEntity.getValue());
    }
}
