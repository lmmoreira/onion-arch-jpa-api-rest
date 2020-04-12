package br.com.company.logistics.project.driver;

import static br.com.company.logistics.project.driver.DriverAttributeType.FILE_API;
import static br.com.company.logistics.project.driver.DriverAttributeType.FILE_NAME;
import static br.com.company.logistics.project.driver.DriverAttributeType.FILE_PATH;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class DriverAttributeLinkStrategy {

    private final Map<DriverAttributeType, DriverAttributeCommand> lookupStrategy;

    private DriverAttributeLinkStrategy(final UUID driverUuid, final DriverFileService driverFileService) {
        this.lookupStrategy = ImmutableMap.of(FILE_PATH,
            new DriverAttributeFilePathCommand(driverFileService),
            FILE_API,
            new DriverAttributeFileAPICommand(driverUuid),
            FILE_NAME,
            new DriverAttributeFileNameCommand());
    }

    public static DriverAttributeLinkStrategy of(final UUID driverUuid, final DriverFileService driverFileService) {
        return new DriverAttributeLinkStrategy(driverUuid, driverFileService);
    }

    public DriverAttributeCommand toStrategy(final DriverAttributeType linkStrategy) {
        return lookupStrategy.getOrDefault(linkStrategy, new DriverAttributeFileNameCommand());
    }
}