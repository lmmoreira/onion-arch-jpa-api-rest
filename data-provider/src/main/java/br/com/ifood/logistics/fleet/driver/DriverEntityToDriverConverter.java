package br.com.company.logistics.project.driver;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class DriverEntityToDriverConverter implements Converter<DriverEntity, Driver> {

    private static final String ANONYMIZED_PHONE = "+9999999999999";
    private static final String ANONYMIZED_EMAIL = "@anonymized.invalid";
    private static final String ANONYMIZED_CPF = "999-999-999-99";
    private static final String ANONYMIZED_IMAGE =
        "static.company.com.br/imagens/ce/logistics/drivers/default-driver-image.jpeg";

    private final ConfigurableConversionService conversionService;

    @PostConstruct
    void postConstruct() {
        conversionService.addConverter(this);
    }

    @Override
    public Driver convert(final DriverEntity entity) {
        return fillAnonymousAttributes(Driver.of(entity.getUuid(),
            entity.getExternalId(),
            entity.getTenant(),
            entity.getDeliveryExternalSystem(),
            entity.getExternalUpdatedAt(),
            entity.getAnonymizedAt(),
            entity.getUserUuid(),
            getAttributes(entity)));
    }

    private Set<DriverAttribute> getAttributes(final DriverEntity entity) {
        return Optional.ofNullable(entity.getAttributes())
                .map(attributeEntities -> attributeEntities.stream()
                        .map(ae -> DriverAttribute.of(ae.getName(), ae.getValue(), ae.getUpdatedAt()))
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

    private Driver fillAnonymousAttributes(final Driver driver) {

        if (Objects.nonNull(driver.getAnonymizedAt())) {
            final Set<DriverAttribute> driverAttributeSet = new HashSet<>();
            driverAttributeSet.add(DriverAttribute.of(DriverAttributeName.FULL_NAME, driver.getUuid().toString(), DriverAttributeType.TEXT));
            driverAttributeSet.add(DriverAttribute.of(DriverAttributeName.PHONE, ANONYMIZED_PHONE, DriverAttributeType.TEXT));
            driverAttributeSet.add(DriverAttribute.of(DriverAttributeName.EMAIL, driver.getUuid().toString().concat(ANONYMIZED_EMAIL) , DriverAttributeType.TEXT));
            driverAttributeSet.add(DriverAttribute.of(DriverAttributeName.CPF, ANONYMIZED_CPF, DriverAttributeType.TEXT));
            driverAttributeSet.add(DriverAttribute.of(DriverAttributeName.WORKER_PHOTO, ANONYMIZED_IMAGE, DriverAttributeType.FILE_API));
            driverAttributeSet.add(DriverAttribute.of(DriverAttributeName.DRIVERS_LICENSE_PHOTO, ANONYMIZED_IMAGE, DriverAttributeType.FILE_API));
            driverAttributeSet.add(DriverAttribute.of(DriverAttributeName.IDENTITY_DOCUMENT_BACK_PHOTO, ANONYMIZED_IMAGE, DriverAttributeType.FILE_API));
            driverAttributeSet.add(DriverAttribute.of(DriverAttributeName.IDENTITY_DOCUMENT_FRONT_PHOTO, ANONYMIZED_IMAGE, DriverAttributeType.FILE_API));
            driverAttributeSet.add(DriverAttribute.of(DriverAttributeName.BACKGROUND_CHECK_FILE, ANONYMIZED_IMAGE, DriverAttributeType.FILE_API));
            driverAttributeSet.add(DriverAttribute.of(DriverAttributeName.VEHICLE_DOCUMENT_PHOTO, ANONYMIZED_IMAGE, DriverAttributeType.FILE_API));
            driverAttributeSet.add(DriverAttribute.of(DriverAttributeName.FISCAL_DOCUMENT_PHOTO, ANONYMIZED_IMAGE, DriverAttributeType.FILE_API));
            driverAttributeSet.add(DriverAttribute.of(DriverAttributeName.BANK_DOCUMENT_PHOTO, ANONYMIZED_IMAGE, DriverAttributeType.FILE_API));
            driverAttributeSet.add(DriverAttribute.of(DriverAttributeName.VEHICLE_LICENSE_PLATE_PHOTO, ANONYMIZED_IMAGE, DriverAttributeType.FILE_API));
            return Driver.of(driver.getUuid(), driver.getExternalId(), driver.getTenant(), driver.getDeliveryExternalSystem(), driver.getExternalUpdatedAt(), driver.getAnonymizedAt(), driver.getUserUuid(), driverAttributeSet);
        }
        return driver;
    }
}
