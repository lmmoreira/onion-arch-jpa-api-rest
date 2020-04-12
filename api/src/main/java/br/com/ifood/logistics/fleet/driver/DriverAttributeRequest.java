package br.com.company.logistics.project.driver;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class DriverAttributeRequest {

    private final String name;
    private final String value;

    @JsonCreator
    public DriverAttributeRequest(@JsonProperty("name") final String name, @JsonProperty("value") final String value) {
        this.name = name;
        this.value = value;
    }

}
