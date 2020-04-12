package br.com.company.logistics.project.driver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DriverAttributeSearchOperator {
    
    FULL_NAME(SearchOperation.ATTRIBUTE_MATCH_LIKE);

    private final SearchOperation searchOperation;
}
