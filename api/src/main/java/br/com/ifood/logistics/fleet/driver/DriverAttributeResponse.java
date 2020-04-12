package br.com.company.logistics.project.driver;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

import lombok.Getter;

@Getter
public class DriverAttributeResponse {

    private final String name;
    private final String value;
    private final String type;
    private final ZonedDateTime updatedAt;

    @JsonCreator
    public DriverAttributeResponse(@JsonProperty("name") final String name, @JsonProperty("value") final String value,
            @JsonProperty("type") final String type, @JsonProperty("updatedAt") final ZonedDateTime updatedAt) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.updatedAt = updatedAt;
    }
}
