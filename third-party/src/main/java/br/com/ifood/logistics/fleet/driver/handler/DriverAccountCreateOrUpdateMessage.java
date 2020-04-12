package br.com.company.logistics.project.driver.handler;

import lombok.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Value
public class DriverAccountCreateOrUpdateMessage {

    UUID uuid;
    String externalId;
    String tenant;
    String deliveryExternalSystem;
    ZonedDateTime externalUpdatedAt;
    Set<Attribute> attributes;

    @JsonCreator
    public DriverAccountCreateOrUpdateMessage(@JsonProperty("uuid") final UUID uuid,
                                              @JsonProperty("externalId") final String externalId,
                                              @JsonProperty("tenant") final String tenant,
                                              @JsonProperty("deliveryExternalSystem") final String deliveryExternalSystem,
                                              @JsonProperty("externalUpdatedAt") final ZonedDateTime externalUpdatedAt,
                                              @JsonProperty("attributes") final Set<Attribute> attributes) {
        this.uuid = uuid;
        this.externalId = externalId;
        this.tenant = tenant;
        this.deliveryExternalSystem = deliveryExternalSystem;
        this.externalUpdatedAt = externalUpdatedAt;
        this.attributes = attributes;
    }

    @Value
    public static class Attribute {
        String name;
        String value;

        @JsonCreator
        public Attribute(@JsonProperty("name") final String name, @JsonProperty("value") final String value) {
            this.name = name;
            this.value = value;
        }
    }
}
