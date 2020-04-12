package br.com.company.logistics.project.driver.handler;

import static java.util.stream.Collectors.toSet;

import com.newrelic.api.agent.NewRelic;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.stereotype.Component;

import java.util.Set;

import javax.annotation.PostConstruct;

import br.com.company.logistics.project.common.DeliveryExternalSystemResolver;
import br.com.company.logistics.project.common.TenantIdentifierHolder;
import br.com.company.logistics.project.driver.Driver;
import br.com.company.logistics.project.driver.DriverAttribute;
import br.com.company.logistics.project.driver.DriverAttributeName;
import br.com.company.logistics.project.driver.handler.DriverAccountCreateOrUpdateMessage.Attribute;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Component
public class DriverAccountCreateOrUpdateMessageToDriverConverter
        implements Converter<DriverAccountCreateOrUpdateMessage, Driver> {

    private final ConfigurableConversionService conversionService;
    private final TenantIdentifierHolder tenantIdentifierHolder;
    private final DeliveryExternalSystemResolver deliveryExternalSystemResolver;

    @PostConstruct
    void postConstruct() {
        conversionService.addConverter(this);
    }

    @Override
    public Driver convert(final DriverAccountCreateOrUpdateMessage message) {
        final String tenant = tenantIdentifierHolder.getValue(message.getTenant());
        final String deliveryExternalSystem =
            deliveryExternalSystemResolver.formatDeliveryExternalSystem(tenant, message.getDeliveryExternalSystem());
        return Driver.builder()
                .uuid(message.getUuid())
                .externalId(message.getExternalId())
                .tenant(tenant)
                .deliveryExternalSystem(deliveryExternalSystem)
                .externalUpdatedAt(message.getExternalUpdatedAt())
                .attributes(convertAttributes(message.getAttributes()))
                .build();
    }

    private Set<DriverAttribute> convertAttributes(final Set<Attribute> attributes) {
        return attributes.stream()
                .filter(a -> existsAttributeName(a.getName()))
                .map(a -> DriverAttribute.of(DriverAttributeName.valueOf(a.getName()), a.getValue()))
                .collect(toSet());
    }

    private boolean existsAttributeName(final String name) {
        if (EnumUtils.isValidEnum(DriverAttributeName.class, name)) {
            return true;
        }
        final String message = String.format("Attribute not found: %s ", name);
        log.warn(message);
        NewRelic.noticeError(message, true);
        return false;
    }

}
