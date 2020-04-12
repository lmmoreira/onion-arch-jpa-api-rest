package br.com.company.logistics.project.driver;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.company.logistics.project.common.Paginator;
import lombok.AllArgsConstructor;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.EnumUtils.getEnum;

@AllArgsConstructor
@Repository
class DriverRepositoryImpl implements DriverRepository {

    private final DriverEntityRepository driverEntityRepository;
    private final DriverAttributeEntityRepository driverAttributeEntityRepository;
    private final ConversionService conversionService;

    @Override
    public Driver save(final Driver driver) {
        final DriverEntity driverEntity = toDriverEntity(driver);
        return toDriver(driverEntityRepository.save(driverEntity));
    }

    @Override
    public DriverAttribute saveDriverAttribute(final UUID driverUuid, final DriverAttributeName name,
                                               final String value) {
        final DriverAttributeEntity driverAttributeEntity =
            driverAttributeEntityRepository.findByDriverUuidAndName(driverUuid, name).map(da -> {
                da.setValue(value);
                return da;
            }).orElse(DriverAttributeEntity.of(UUID.randomUUID(), name, value));

        driverAttributeEntity.setDriver(driverEntityRepository.getOne(driverUuid));
        return toDriverAttribute(driverAttributeEntityRepository.save(driverAttributeEntity));
    }

    @Override
    public void deleteDriverAttributeBy(final UUID driverUuid, final DriverAttributeName name) {
        driverAttributeEntityRepository.deleteByDriverUuidAndName(driverUuid, name);
    }

    @Override
    public void deleteBy(final UUID uuid) {
        driverEntityRepository.deleteById(uuid);
    }

    @Override
    public Paginator<Driver> findDriversAndAttributesBy(final int page, final int size, final List<UUID> driverIds,
                                                        final Map<String, String> attributes,
                                                        final List<String> attributesSelection,
                                                        final String deliveryExternalSystem) {
        final Pageable pageable = PageRequest.of(page, size);
        final DriverEntitySpecification specification =
            createDriverEntitySpecification(driverIds, attributes, deliveryExternalSystem);
        return PageToPaginatorConverter.convert(driverEntityRepository.findAll(specification,
            EntityGraph.EntityGraphType.LOAD,
            "driverEntityGraph",
            toDriverAttributesName(attributesSelection),
            pageable)).map(this::toDriver);
    }

    private DriverEntitySpecification createDriverEntitySpecification(final List<UUID> driverIds, final Map<String, String> attributes, final String deliveryExternalSystem) {
        final DriverEntitySpecification specification = new DriverEntitySpecification();
        specification.addSearchCriteria("uuid", driverIds, SearchOperation.IN);
        specification.addSearchCriteria("deliveryExternalSystem", deliveryExternalSystem, SearchOperation.EQUAL);
        createDriverAttributesSpecification(attributes, specification);
        return specification;
    }

    private void createDriverAttributesSpecification(final Map<String, String> attributes, final DriverEntitySpecification specification) {
        attributes.forEach(
            (name, value) -> {
                specification.addSearchCriteria(name, value, getSearchOperation(name));
            });
    }

    private SearchOperation getSearchOperation(final String name) {
        return ofNullable(getEnum(DriverAttributeSearchOperator.class, name))
            .map(DriverAttributeSearchOperator::getSearchOperation)
            .orElse(SearchOperation.ATTRIBUTE_MATCH_EQUAL);
    }

    @Override
    public Optional<Driver> findDriverAndAttributesBy(final UUID uuid) {
        return driverEntityRepository.findDriverAndAttributesBy(uuid).map(this::toDriver);
    }

    @Override
    public Optional<Driver> findDriverAndAttributesBy(final UUID uuid, final List<String> attributesSelection) {
        final List<DriverAttributeName>  attributeNames = toDriverAttributesName(attributesSelection);
        final DriverEntitySpecification specification = new DriverEntitySpecification();
        specification.addSearchCriteria("uuid", uuid, SearchOperation.EQUAL);
        specification.addSearchCriteria("name", attributeNames, SearchOperation.ATTRIBUTE_NAME_MATCH);
        return driverEntityRepository
                .findDriverAndAttributesBy(specification, EntityGraph.EntityGraphType.LOAD, "driverEntityGraph")
                .map(driverEntity -> {
                    final Driver driver = toDriver(driverEntity);
                    driver.getAttributes().removeAll(getRemovableAttributes(attributeNames, driver));
                    return driver;
                });
    }

    private List<DriverAttributeName> toDriverAttributesName(final List<String> attributesSelection) {
        return attributesSelection.stream()
                .map(DriverAttributeName::getAttribute)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DriverAttribute> findDriverAttribute(final UUID driverId, final DriverAttributeName name) {
        return driverAttributeEntityRepository.findByDriverUuidAndName(driverId, name).map(this::toDriverAttribute);
    }

    @Override
    public boolean existsById(final UUID driverId) {
        return driverEntityRepository.existsById(driverId);
    }

    private Driver toDriver(final DriverEntity entity) {
        return conversionService.convert(entity, Driver.class);
    }

    private DriverEntity toDriverEntity(final Driver driver) {
        return conversionService.convert(driver, DriverEntity.class);
    }

    private DriverAttribute toDriverAttribute(final DriverAttributeEntity driverAttributeEntity) {
        return conversionService.convert(driverAttributeEntity, DriverAttribute.class);
    }

    private Set<DriverAttribute> getRemovableAttributes(final List<DriverAttributeName> attributeNames, final Driver driver) {
        if(attributeNames.isEmpty()){
            return Collections.emptySet();
        }
        return driver.getAttributes()
                .stream()
                .filter(attribute -> !attributeNames.contains(attribute.getName()))
                .collect(Collectors.toSet());
    }
}
