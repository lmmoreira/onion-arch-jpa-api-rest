package br.com.company.logistics.project.driver;

import static br.com.company.logistics.project.driver.DriverAttributeType.FILE_API;
import static br.com.company.logistics.project.driver.DriverAttributeType.TEXT;

import java.time.ZonedDateTime;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(of = "name")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DriverAttribute {

    @Getter
    @NonNull
    private final DriverAttributeName name;

    private final String value;

    @Getter
    @NonNull
    private final DriverAttributeType type;

    @Getter
    private final ZonedDateTime updatedAt;

    public static DriverAttribute of(@NonNull final DriverAttributeName name, final String value) {
        return new DriverAttribute(name, value, getDefaultType(name), null);
    }

    public static DriverAttribute of(@NonNull final DriverAttributeName name, final String value,
                                     @NonNull final DriverAttributeType type) {
        return new DriverAttribute(name, value, type, null);
    }

    public static DriverAttribute of(@NonNull final DriverAttributeName name, final String value,
                                     final ZonedDateTime updatedAt) {
        return new DriverAttribute(name, value, getDefaultType(name), updatedAt);
    }

    public static DriverAttribute of(@NonNull final DriverAttributeName name, final String value,
                                     @NonNull final DriverAttributeType type, final ZonedDateTime updatedAt) {
        return new DriverAttribute(name, value, type, updatedAt);
    }

    private static DriverAttributeType getDefaultType(final DriverAttributeName name) {
        return name.isFile() ? FILE_API : TEXT;
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }

    public boolean hasValueAndIsFile() {
        return getValue().isPresent() && getName().isFile();
    }
}
