package br.com.company.logistics.project.driver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class DriverAggregatorResponse {
    private final Driver driver;
    private final DriverAttributeType strategy;
}
