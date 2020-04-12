package br.com.company.logistics.project.driver;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class DriverAnonymizationEntityToDriverAnonymizationConverter implements Converter<DriverAnonymizationEntity, DriverAnonymization> {

    private final ConfigurableConversionService conversionService;

    @PostConstruct
    void postConstruct() {
        conversionService.addConverter(this);
    }

    @Override
    public DriverAnonymization convert(final DriverAnonymizationEntity driverAnonymizationEntity) {
        return DriverAnonymization.of(driverAnonymizationEntity.getUuid(),
            driverAnonymizationEntity.getDriverUuid(),
            driverAnonymizationEntity.getUserUuid(),
            driverAnonymizationEntity.getCreatedAt(),
            driverAnonymizationEntity.getAnonymizedAt());
    }

}
