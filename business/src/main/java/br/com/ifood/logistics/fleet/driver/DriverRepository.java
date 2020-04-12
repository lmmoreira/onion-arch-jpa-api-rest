package br.com.company.logistics.project.driver;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import br.com.company.logistics.project.common.Paginator;

interface DriverRepository {
    
    Driver save(Driver driver);
    
    DriverAttribute saveDriverAttribute(UUID driverUuid, DriverAttributeName name, String value);

    void deleteDriverAttributeBy(UUID driverUuid, DriverAttributeName name);

    void deleteBy(UUID uuid);

    Paginator<Driver> findDriversAndAttributesBy(int page, int size, List<UUID> driverIds,
                                                 Map<String, String> attributes, List<String> attributesSelection,
                                                 String deliveryExternalSystem);

    Optional<Driver> findDriverAndAttributesBy(UUID uuid);

    Optional<Driver> findDriverAndAttributesBy(UUID uuid, List<String> attributesNames);

    Optional<DriverAttribute> findDriverAttribute(UUID driverUuid, DriverAttributeName name);

    boolean existsById(UUID driverId);
}
