package br.com.company.logistics.project.driver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface DriverEntityRepository extends JpaRepository<DriverEntity, UUID>, DriverEntityRepositoryCustom {

    Optional<DriverEntity> findByUuid(UUID uuid);
    
    @Query("select d.uuid from DriverEntity d ")
    Page<UUID> findIdsBy(Pageable pageable);
    
    @Query("select d.uuid from DriverEntity d where d.uuid in :driverIds")
    Page<UUID> findIdsBy(List<UUID> driverIds, Pageable pageable);

    @EntityGraph(attributePaths = {"attributes"})
    @Query("select d from DriverEntity d where d.uuid in :driverIds")
    List<DriverEntity> findDriverAndAttributesBy(List<UUID> driverIds);
    
    @EntityGraph(attributePaths = {"attributes"})
    @Query("select d from DriverEntity d where d.uuid = :uuid")
    Optional<DriverEntity> findDriverAndAttributesBy(UUID uuid);

}
