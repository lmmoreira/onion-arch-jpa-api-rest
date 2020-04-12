package br.com.company.logistics.project.driver;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class DriverEntitySpecification implements Specification<DriverEntity> {

    private final List<SearchCriteriaDriverEntity> attributesFilter;

    public DriverEntitySpecification() {
        this.attributesFilter = new ArrayList<>();
    }


    public void addSearchCriteria(final String key, final Object value, final SearchOperation searchOperation) {
        attributesFilter.add(new SearchCriteriaDriverEntity(key, value, searchOperation));
    }

    @Override
    public Predicate toPredicate(final Root<DriverEntity> root, final CriteriaQuery<?> criteriaQuery,
                                 final CriteriaBuilder builder) {
        return builder.and(attributesFilter.stream()
                .filter(this::hasValue)
                .map(criteria -> criteria.getOperation()
                        .createPredicate(root, builder, criteria.getKey(), criteria.getValue()))
                .toArray(Predicate[]::new));
    }

    private boolean hasValue(final SearchCriteriaDriverEntity criteria) {
        return isNotEmpty(criteria.getValue());
    }
}
