package br.com.company.logistics.project.driver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchCriteriaDriverEntity {

    private final String key;
    private final Object value;
    private final SearchOperation operation;

}
