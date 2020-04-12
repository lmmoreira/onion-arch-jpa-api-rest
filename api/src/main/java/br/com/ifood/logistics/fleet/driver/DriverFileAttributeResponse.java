package br.com.company.logistics.project.driver;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class DriverFileAttributeResponse {

    private final String name;
    private final String url;
    private final String fileName;

    @JsonCreator
    public DriverFileAttributeResponse(@JsonProperty("name") final String name, @JsonProperty("url") final String url,
            @JsonProperty("fileName") final String fileName) {
        this.name = requireNonNull(name);
        this.url = requireNonNull(url);
        this.fileName = requireNonNull(fileName);
    }
}
