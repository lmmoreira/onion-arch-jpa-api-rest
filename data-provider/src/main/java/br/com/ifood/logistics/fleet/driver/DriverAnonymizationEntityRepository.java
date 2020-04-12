package br.com.company.logistics.project.driver;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface DriverAnonymizationEntityRepository extends JpaRepository<DriverAnonymizationEntity, UUID> {

    List<DriverAnonymizationEntity> findByCreatedAtLessThanAndAnonymizedAtIsNull(ZonedDateTime createdAt);

    Optional<DriverAnonymizationEntity> findByDriverUuid(UUID driverUuid);

}
