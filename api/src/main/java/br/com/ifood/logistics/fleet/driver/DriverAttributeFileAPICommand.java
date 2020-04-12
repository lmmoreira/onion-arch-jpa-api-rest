package br.com.company.logistics.project.driver;

import static br.com.company.logistics.project.driver.DriverAttributeType.FILE_API;
import static br.com.company.logistics.project.driver.DriverAttributeType.TEXT;

import java.util.UUID;

public class DriverAttributeFileAPICommand implements DriverAttributeCommand {

    private final UUID driverId;

    public DriverAttributeFileAPICommand(final UUID driverId) {
        this.driverId = driverId;
    }

    @Override
    public DriverAttribute execute(final DriverAttribute attribute) {
        if (attribute.hasValueAndIsFile()) {
            return DriverAttribute.of(attribute.getName(), createPath(attribute), FILE_API, attribute.getUpdatedAt());
        }
        return DriverAttribute
                .of(attribute.getName(), attribute.getValue().orElse(null), TEXT, attribute.getUpdatedAt());
    }

    private String createPath(final DriverAttribute driverAttribute) {
        return String.format("/api/logistics/project/drivers/%s/attributes/%s/file", driverId, driverAttribute.getName());
    }
}
