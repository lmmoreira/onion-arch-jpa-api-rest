package br.com.company.logistics.project.driver;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class DriverAnonymizationToDriverAnonymizationEntityConverter implements Converter<DriverAnonymization, DriverAnonymizationEntity> {

    private final ConfigurableConversionService conversionService;

    @PostConstruct
    void postConstruct() {
        conversionService.addConverter(this);
    }

    @Override
    public DriverAnonymizationEntity convert(final DriverAnonymization driverAnonymization) {
        final DriverAnonymizationEntity entity = DriverAnonymizationEntity.of(driverAnonymization.getUuid(),
            driverAnonymization.getDriverUuid(),
            driverAnonymization.getUserUuid());
        entity.setAnonymizedAt(driverAnonymization.getAnonymizedAt());
        return entity;
    }

}
