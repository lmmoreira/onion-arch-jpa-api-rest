package br.com.company.logistics.project.driver;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface SearchOperationPredicate {
    
    Predicate createPredicate(Root<DriverEntity> root, CriteriaBuilder builder, String key, Object value);
}
