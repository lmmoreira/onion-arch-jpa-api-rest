package br.com.company.logistics.project.driver;

import static br.com.company.logistics.project.driver.DriverAttributeType.FILE_NAME;
import static br.com.company.logistics.project.driver.DriverAttributeType.TEXT;

public class DriverAttributeFileNameCommand implements DriverAttributeCommand {

    @Override
    public DriverAttribute execute(final DriverAttribute attribute) {
        return DriverAttribute.of(attribute.getName(),
            attribute.getValue().orElse(null),
            attribute.getName().isFile() ? FILE_NAME : TEXT,
            attribute.getUpdatedAt());
    }
}
