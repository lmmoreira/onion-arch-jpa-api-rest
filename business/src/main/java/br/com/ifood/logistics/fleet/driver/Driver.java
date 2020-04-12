package br.com.company.logistics.project.driver;

import static java.util.Optional.empty;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Builder
@ToString
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "of")
public class Driver {

    @NonNull
    private final UUID uuid;

    private final String externalId;

    @NonNull
    private final String tenant;

    @NonNull
    private final String deliveryExternalSystem;

    @NonNull
    private final ZonedDateTime externalUpdatedAt;

    private final ZonedDateTime anonymizedAt;

    private final UUID userUuid;

    private final Set<DriverAttribute> attributes;

    public static Driver of(@NonNull final UUID uuid, final String externalId, @NonNull final String tenant,
                            @NonNull final String deliveryExternalSystem,
                            @NonNull final ZonedDateTime externalUpdatedAt, final UUID userUuid,
                            @NonNull final Set<DriverAttribute> attributes) {
        return of(uuid, externalId, tenant, deliveryExternalSystem, externalUpdatedAt, null, userUuid, attributes);
    }

    public Optional<String> getAttributeValue(final DriverAttributeName driverAttributeName) {
        return attributes.stream()
                .filter(attribute -> attribute.getName().equals(driverAttributeName))
                .map(DriverAttribute::getValue)
                .findFirst()
                .orElse(empty());
    }

}
