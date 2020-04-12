package br.com.company.logistics.project.driver;


import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class DriverToDriverEntityConverter implements Converter<Driver, DriverEntity> {

    private final ConfigurableConversionService conversionService;
    private final DriverEntityRepository driverEntityRepository;

    @PostConstruct
    void postConstruct() {
        conversionService.addConverter(this);
    }

    @Override
    public DriverEntity convert(final Driver driver) {
        final DriverEntity driverEntity =
            driverEntityRepository.findDriverAndAttributesBy(driver.getUuid()).map(entity -> {
                deleteNotInformedAttributes(entity, driver.getAttributes());
                updateAlreadySavedAttributes(entity, driver.getAttributes());
                createNewAttributes(entity, driver.getAttributes());
                return entity;
            }).orElse(createDriverEntity(driver));
        driverEntity.setExternalUpdatedAt(driver.getExternalUpdatedAt());
        driverEntity.setAnonymizedAt(driver.getAnonymizedAt());
        driverEntity.setUserUuid(driver.getUserUuid());
        return driverEntity;
    }

    private DriverEntity createDriverEntity(final Driver driver) {
        final DriverEntity driverEntity = DriverEntity.of(driver.getUuid(),
            driver.getTenant(),
            driver.getExternalUpdatedAt());
        driverEntity.setExternalId(driver.getExternalId());
        driverEntity.setDeliveryExternalSystem(driver.getDeliveryExternalSystem());
        driverEntity.addDriverAttributeEntities(convertAttributes(driver.getAttributes()));
        return driverEntity;
    }

    private Set<DriverAttributeEntity> convertAttributes(final Set<DriverAttribute> driverAttributes) {
        return Optional.ofNullable(driverAttributes)
                .map(attrs -> attrs.stream()
                        .filter(da -> da.getValue().isPresent())
                        .map(da -> DriverAttributeEntity.of(UUID.randomUUID(), da.getName(), da.getValue().get()))
                        .collect(Collectors.toSet()))
                .orElse(new HashSet<>());
    }

    private void updateAlreadySavedAttributes(final DriverEntity driverEntity,
                                              final Set<DriverAttribute> driverAttributes) {

        final Map<DriverAttributeName, String> driverAttributeMap = driverAttributes.stream()
                .filter(driverAttribute -> driverAttribute.getValue().isPresent())
                .collect(Collectors.toMap(DriverAttribute::getName, (attr) -> attr.getValue().get()));

        driverEntity.getAttributes().forEach(driverAttributeEntity -> {
            if (driverAttributeMap.containsKey(driverAttributeEntity.getName()) &&
                    !driverAttributeMap.get(driverAttributeEntity.getName()).equals(driverAttributeEntity.getValue())) {
                driverAttributeEntity.setValue(driverAttributeMap.get(driverAttributeEntity.getName()));
            }
        });
    }

    private void createNewAttributes(final DriverEntity driverEntity, final Set<DriverAttribute> driverAttributes) {
        final Set<DriverAttribute> newDriverAttributes = driverAttributes.stream()
                .filter(driverAttribute -> driverEntity.getAttributes()
                        .stream()
                        .map(DriverAttributeEntity::getName)
                        .noneMatch(name -> name.equals(driverAttribute.getName())))
                .collect(Collectors.toSet());

        driverEntity.addDriverAttributeEntities(convertAttributes(newDriverAttributes));
    }

    private void deleteNotInformedAttributes(final DriverEntity driverEntity,
                                             final Set<DriverAttribute> driverAttributes) {

        final Map<DriverAttributeName, String> driverAttributeMap = driverAttributes.stream()
                .filter(driverAttribute -> driverAttribute.getValue().isPresent())
                .collect(Collectors.toMap(DriverAttribute::getName, attr -> attr.getValue().get()));

        final Set<DriverAttributeEntity> driverEntitiesAttributesToRemove = driverEntity.getAttributes()
                .stream()
                .filter(a -> notContains(driverAttributeMap, a.getName()))
                .filter(a -> isDefaultGroup(a.getName()))
                .collect(Collectors.toSet());

        driverEntity.getAttributes().removeAll(driverEntitiesAttributesToRemove);
    }

    private boolean notContains(final Map<DriverAttributeName, String> driverAttributeMap,
                                final DriverAttributeName name) {
        return !driverAttributeMap.containsKey(name);
    }

    private boolean isDefaultGroup(final DriverAttributeName name) {
        return DriverAttributeGroup.DEFAULT.equals(name.getGroup());
    }

}
