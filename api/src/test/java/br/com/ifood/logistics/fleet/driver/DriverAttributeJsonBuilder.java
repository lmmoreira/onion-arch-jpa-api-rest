package br.com.company.logistics.project.driver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DriverAttributeJsonBuilder {
    protected final Map<String, Object> jsonAsMap;
    private final ObjectMapper mapper = new ObjectMapper();

    public DriverAttributeJsonBuilder() {
        this.jsonAsMap = new HashMap<>();
        jsonAsMap.put("value", "http://s3.company.com.br/foto_woker.jpg");
    }

    public DriverAttributeJsonBuilder uuid(final UUID uuid) {
        jsonAsMap.put("uuid", uuid);
        return this;
    }

    public String build() {
        try {
            return mapper.writeValueAsString(jsonAsMap);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException("Cannot convert object in Json: " + jsonAsMap, e);
        }
    }
}
