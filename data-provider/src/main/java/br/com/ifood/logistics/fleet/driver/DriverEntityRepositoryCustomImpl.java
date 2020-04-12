package br.com.company.logistics.project.driver;

import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.EntityType;

@Repository
public class DriverEntityRepositoryCustomImpl extends SimpleJpaRepository<DriverEntity, UUID>
        implements DriverEntityRepositoryCustom {

    private final EntityManager entityManager;

    public DriverEntityRepositoryCustomImpl(final EntityManager entityManager) {
        super(DriverEntity.class, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public Optional<DriverEntity> findDriverAndAttributesBy(final Specification<DriverEntity> driverEntitySpecification,
                                                            final EntityGraph.EntityGraphType entityGraphType,
                                                            final String entityGraphName) {
        if (isValid(entityGraphType, entityGraphName)) {
            final TypedQuery<DriverEntity> queryDrivers =
                getQuery(driverEntitySpecification, DriverEntity.class, Sort.unsorted());
            queryDrivers.setHint(entityGraphType.getKey(), entityManager.getEntityGraph(entityGraphName));
            return queryDrivers.getResultList().stream().findFirst();
        }
        return findOne(driverEntitySpecification);
    }

    @Override
    public Page<DriverEntity> findAll(final Specification<DriverEntity> driverEntitySpecification,
                                      final EntityGraph.EntityGraphType entityGraphType, final String entityGraphName,
                                      final List<DriverAttributeName> attributesSelection, final Pageable pageable) {
        if (isValid(entityGraphType, entityGraphName)) {
            final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<UUID> criteriaQuery = criteriaBuilder.createQuery(UUID.class);
            final Root<DriverEntity> root = criteriaQuery.from(DriverEntity.class);
            final Predicate predicate = driverEntitySpecification.toPredicate(root, criteriaQuery, criteriaBuilder);
            criteriaQuery.where(predicate).select(root.get("uuid"));
            final List<UUID> driverIds = findDriverIds(pageable, criteriaQuery);

            final TypedQuery<DriverEntity> queryDrivers = getDriverEntityTypedQuery(entityGraphType,
                entityGraphName,
                attributesSelection,
                criteriaBuilder,
                driverIds);
            
            final List<DriverEntity> drivers =
                isEmpty(driverIds) ? Collections.emptyList() : queryDrivers.getResultList();

            final TypedQuery<Long> countQuery = getCountQuery(driverEntitySpecification, DriverEntity.class);

            return new PageImpl<>(drivers, pageable, countQuery.getSingleResult());
        }
        return findAll(driverEntitySpecification, pageable);
    }

    private TypedQuery<DriverEntity> getDriverEntityTypedQuery(final EntityGraph.EntityGraphType entityGraphType,
                                                               final String entityGraphName,
                                                               final List<DriverAttributeName> attributeSelection,
                                                               final CriteriaBuilder criteriaBuilder,
                                                               final List<UUID> driverIds) {
        final CriteriaQuery<DriverEntity> query = criteriaBuilder.createQuery(DriverEntity.class);
        final Root<DriverEntity> driverRoot = query.from(DriverEntity.class);
        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(driverRoot.get("uuid").in(driverIds));
        if (!isEmpty(attributeSelection)) {
            addPredicateAttributeSelection(attributeSelection, driverRoot);
        }
        query.where(predicates.toArray(new Predicate[0]));
        final TypedQuery<DriverEntity> queryDrivers = entityManager.createQuery(query);
        queryDrivers.setHint(entityGraphType.getKey(), entityManager.getEntityGraph(entityGraphName));
        return queryDrivers;
    }

    private void addPredicateAttributeSelection(final List<DriverAttributeName> attributeSelection,
                                                final Root<DriverEntity> driverRoot) {
        final EntityType<DriverEntity> driverModel = driverRoot.getModel();
        final SetJoin<DriverEntity, ?> attributes = driverRoot.join(driverModel.getSet("attributes"), JoinType.LEFT);
        attributes.on(attributes.get("name").in(attributeSelection));
    }

    private List<UUID> findDriverIds(final Pageable pageable, final CriteriaQuery<UUID> criteriaQuery) {
        return entityManager.createQuery(criteriaQuery)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    private boolean isValid(final EntityGraph.EntityGraphType entityGraphType, final String entityGraphName) {
        return !isNull(entityGraphType) && !isNull(entityGraphName);
    }
}
