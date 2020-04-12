package br.com.company.logistics.project.driver;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(of = "uuid")
@ToString(exclude = "attributes")
class DriverRequest {

    private final UUID uuid;
    private final String externalId;
    private final String tenant;
    private final String deliveryExternalSystem;
    private final ZonedDateTime externalUpdatedAt;
    private final UUID userUuid;
    private final Set<DriverAttributeRequest> attributes;

    @JsonCreator
    public DriverRequest(@JsonProperty("uuid") final UUID uuid, @JsonProperty("externalId") final String externalId,
            @JsonProperty("tenant") final String tenant,
            @JsonProperty("deliveryExternalSystem") final String deliveryExternalSystem,
            @JsonProperty("externalUpdatedAt") final ZonedDateTime externalUpdatedAt,
            @JsonProperty("userUuid") final UUID userUuid,
            @JsonProperty("attributes") final Set<DriverAttributeRequest> attributes) {
        this.uuid = uuid;
        this.externalId = externalId;
        this.tenant = tenant;
        this.deliveryExternalSystem = deliveryExternalSystem;
        this.externalUpdatedAt = externalUpdatedAt;
        this.userUuid = userUuid;
        this.attributes = attributes;
    }
}
