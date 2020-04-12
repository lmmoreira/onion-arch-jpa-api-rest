package br.com.company.logistics.project.driver;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public enum SearchOperation implements SearchOperationPredicate {

    EQUAL {
        @Override
        public Predicate createPredicate(final Root<DriverEntity> root, final CriteriaBuilder builder, final String key,
                                         final Object value) {
            return builder.equal(root.get(key), value);
        }
    },
    IN {
        @Override
        public Predicate createPredicate(final Root<DriverEntity> root, final CriteriaBuilder builder, final String key,
                                         final Object value) {
            return builder.in(root.get(key)).value(value);
        }
    },
    ATTRIBUTE_MATCH_LIKE {
        @Override
        public Predicate createPredicate(final Root<DriverEntity> root, final CriteriaBuilder builder, final String key,
                                         final Object value) {
            final Join<DriverEntity, DriverAttributeEntity> join = getJoinAttributes(root);
            final Predicate predicateName = builder.equal(join.get("name"), DriverAttributeName.valueOf(key));
            final Predicate predicateValue = builder.like(builder.lower(join.get("value")),
                    "%" + value.toString().toLowerCase() + "%");
            return builder.and(predicateName, predicateValue);
        }
    },
    ATTRIBUTE_MATCH_EQUAL {
        @Override
        public Predicate createPredicate(final Root<DriverEntity> root, final CriteriaBuilder builder, final String key,
                                         final Object value) {
            final Join<DriverEntity, DriverAttributeEntity> join = getJoinAttributes(root);
            final Predicate predicateName = builder.equal(join.get("name"), DriverAttributeName.valueOf(key));
            final Predicate predicateValue = builder.equal(builder.lower((join.get("value"))),
                    value.toString().toLowerCase());
            return builder.and(predicateName, predicateValue);
        }
    },
    ATTRIBUTE_NAME_MATCH {
        @Override
        public Predicate createPredicate(final Root<DriverEntity> root, final CriteriaBuilder builder, final String key,
                                         final Object value) {
            final Join<DriverEntity, DriverAttributeEntity> join = root.join("attributes", JoinType.LEFT);
            join.on(builder.in(join.get(key)).value(value));
            return builder.conjunction();
        }
    };

    private static Join<DriverEntity, DriverAttributeEntity> getJoinAttributes(final Root<DriverEntity> root) {
        return root.join("attributes");
    }
}
