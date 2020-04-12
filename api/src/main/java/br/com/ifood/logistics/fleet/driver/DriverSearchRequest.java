package br.com.company.logistics.project.driver;

import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;

@Getter
public class DriverSearchRequest {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    private final int page;
    private final int size;
    private final List<UUID> driverIds;
    private final Map<String, String> attributes;
    private final List<String> attributesSelection;
    private final String deliveryExternalSystem;

    @JsonCreator
    public DriverSearchRequest(@JsonProperty("page") final Integer page, @JsonProperty("size") final Integer size,
            @JsonProperty("driverIds") final List<UUID> driverIds,
            @JsonProperty("attributes") final Map<String, String> attributes,
            @JsonProperty("attributesSelection") final List<String> attributesSelection,
            @JsonProperty("deliveryExternalSystem") final String deliveryExternalSystem) {
        this.page = firstNonNull(page, DEFAULT_PAGE);
        this.size = firstNonNull(size, DEFAULT_SIZE);
        this.driverIds = ofNullable(driverIds).map(ImmutableList::copyOf).orElse(ImmutableList.of());
        this.attributes = ofNullable(attributes).map(ImmutableMap::copyOf).orElse(ImmutableMap.of());
        this.attributesSelection =
            ofNullable(attributesSelection).map(ImmutableList::copyOf).orElse(ImmutableList.of());
        this.deliveryExternalSystem = deliveryExternalSystem;
    }
}
