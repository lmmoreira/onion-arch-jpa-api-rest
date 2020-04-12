package br.com.company.logistics.project.driver;

import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DriverAnonymization {

    @NonNull
    private final UUID uuid;

    @NonNull
    private UUID driverUuid;

    @NonNull
    private final String userUuid;

    private final ZonedDateTime createdAt;

    private final ZonedDateTime anonymizedAt;

    public static DriverAnonymization of(@NonNull final UUID uuid, @NonNull final UUID driverUuid, @NonNull final String userUuid) {
        return new DriverAnonymization(uuid, driverUuid, userUuid, null, null);
    }

    public static DriverAnonymization of(@NonNull final UUID uuid, @NonNull final UUID driverUuid, @NonNull final String userUuid, ZonedDateTime createdAt) {
        return new DriverAnonymization(uuid, driverUuid, userUuid, createdAt, null);
    }

    public static DriverAnonymization of(@NonNull final UUID uuid, @NonNull final UUID driverUuid, @NonNull final String userUuid, ZonedDateTime createdAt, ZonedDateTime anonymizedAt) {
        return new DriverAnonymization(uuid, driverUuid, userUuid, createdAt, anonymizedAt);
    }

}
