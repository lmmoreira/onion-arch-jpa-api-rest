package br.com.company.logistics.project.driver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface DriverEntityRepositoryCustom
        extends JpaRepository<DriverEntity, UUID>, JpaSpecificationExecutor<DriverEntity> {

    Optional<DriverEntity> findDriverAndAttributesBy(Specification<DriverEntity> driverEntitySpecification,
                                                     EntityGraph.EntityGraphType entityGraphType, String entityGraphName);

    Page<DriverEntity> findAll(Specification<DriverEntity> driverEntitySpecification,
                               EntityGraph.EntityGraphType entityGraphType, String entityGraphName,
                               List<DriverAttributeName> attributesSelection, Pageable pageable);

}
