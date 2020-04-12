package br.com.company.logistics.project.driver;

import static br.com.company.logistics.project.driver.DriverAttributeType.FILE_PATH;
import static br.com.company.logistics.project.driver.DriverAttributeType.TEXT;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DriverAttributeFilePathCommand implements DriverAttributeCommand {

    private final DriverFileService driverFileService;

    @Override
    public DriverAttribute execute(final DriverAttribute attribute) {
        if (attribute.hasValueAndIsFile()) {
            return DriverAttribute.of(attribute.getName(),
                driverFileService.getUrl(attribute.getValue().get()).toString(),
                FILE_PATH,
                attribute.getUpdatedAt());
        }
        return DriverAttribute
                .of(attribute.getName(), attribute.getValue().orElse(null), TEXT, attribute.getUpdatedAt());
    }
}
