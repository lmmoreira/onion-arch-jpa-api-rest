package br.com.company.logistics.project.driver;

import javax.annotation.PostConstruct;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DriverFileAttributeToDriverFileAttributeResponseConverter
        implements Converter<DriverFileAttribute, DriverFileAttributeResponse> {

    private final ConfigurableConversionService conversionService;

    @PostConstruct
    void postConstruct() {
        conversionService.addConverter(this);
    }

    @Override
    public DriverFileAttributeResponse convert(final DriverFileAttribute driverFileAttribute) {
        return new DriverFileAttributeResponse(driverFileAttribute.getName().name(), driverFileAttribute.getUrl(),
            driverFileAttribute.getFileName());
    }

}
