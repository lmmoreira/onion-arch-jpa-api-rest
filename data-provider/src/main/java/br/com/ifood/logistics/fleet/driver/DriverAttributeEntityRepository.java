package br.com.company.logistics.project.driver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface DriverAttributeEntityRepository extends JpaRepository<DriverAttributeEntity, UUID> {

    Optional<DriverAttributeEntity> findByDriverAndName(final DriverEntity driver, DriverAttributeName name);
    
    @Query("select da from DriverAttributeEntity da inner join da.driver d where d.uuid = :driverUuid and da.name = :name")
    Optional<DriverAttributeEntity> findByDriverUuidAndName(final UUID driverUuid, DriverAttributeName name);
    
    void deleteByDriverUuidAndName(final UUID driverUuid, final DriverAttributeName name);

}
