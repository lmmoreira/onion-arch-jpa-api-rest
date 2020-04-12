package br.com.company.logistics.project.driver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

class DriverJsonBuilder {

    protected final Map<String, Object> jsonAsMap;
    private final ObjectMapper mapper = new ObjectMapper();

    public DriverJsonBuilder() {
        this.jsonAsMap = new HashMap<>();
        jsonAsMap.put("externalId", ThreadLocalRandom.current().nextLong());
        jsonAsMap.put("tenant", "br");
        jsonAsMap.put("deliveryExternalSystem", "project");
        jsonAsMap.put("externalUpdatedAt", ZonedDateTime.now().toString());
        jsonAsMap.put("userUuid", UUID.randomUUID().toString());
        final Faker faker = new Faker();
        jsonAsMap.put("attributes",
                Set.of(new DriverAttributeRequest(DriverAttributeName.FATHERS_NAME.name(), faker.name().fullName()),
                        new DriverAttributeRequest(DriverAttributeName.FULL_NAME.name(), faker.name().fullName()),
                        new DriverAttributeRequest(DriverAttributeName.EMAIL.name(), faker.internet().emailAddress()),
                        new DriverAttributeRequest(DriverAttributeName.PHONE.name(), faker.phoneNumber().cellPhone())));
    }

    public DriverJsonBuilder uuid(final UUID uuid) {
        jsonAsMap.put("uuid", uuid);
        return this;
    }

    public DriverJsonBuilder externalId(final Long externalId) {
        jsonAsMap.put("externalId", externalId);
        return this;
    }

    public DriverJsonBuilder tenant(final String tenant) {
        jsonAsMap.put("tenant", tenant);
        return this;
    }
    
    public DriverJsonBuilder deliveryExternalSystem(final String deliveryExternalSystem) {
        jsonAsMap.put("deliveryExternalSystem", deliveryExternalSystem);
        return this;
    }

    public DriverJsonBuilder attributes(final Set<DriverAttributeRequest> attributes) {
        jsonAsMap.put("attributes", attributes);
        return this;
    }

    public DriverJsonBuilder addAttribute(final DriverAttributeRequest driverAttributeRequest) {
        final Set<DriverAttributeRequest> attributes =
            new HashSet<>(((Set<DriverAttributeRequest>) jsonAsMap.get("attributes")));
        attributes.add(driverAttributeRequest);
        jsonAsMap.put("attributes", attributes);
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
