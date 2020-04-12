package br.com.company.logistics.project.driver;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import br.com.company.logistics.project.common.DriverAccountException;
import br.com.company.logistics.project.common.Paginator;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverAnonymizationService driverAnonymizationService;
    private final DriverRepository driverRepository;
    private final DriverAnonymizationRepository driverAnonymizationRepository;

    private final DriverFileService driverFileService;

    @Transactional(readOnly = true)
    public Paginator<Driver> findDriversAndAttributesBy(final int page, final int size, final List<UUID> driverIds,
                                                        final Map<String, String> attributes,
                                                        final List<String> attributesSelection, 
                                                        final String deliveryExternalSystem) {
        return driverRepository.findDriversAndAttributesBy(page,
            size,
            driverIds,
            attributes,
            attributesSelection,
            deliveryExternalSystem);
    }

    @Transactional(readOnly = true)
    public Optional<Driver> findDriverAndAttributesBy(final UUID uuid, final List<String> attributesSelection) {
        return driverRepository.findDriverAndAttributesBy(uuid, attributesSelection);
    }

    @Transactional(readOnly = true)
    public Optional<Driver> findDriverAndAttributesBy(final UUID uuid) {
        return driverRepository.findDriverAndAttributesBy(uuid);
    }

    @Transactional
    public Driver save(final Driver driver) {
        return driverRepository.save(driver);
    }

    public UUID anonymize(final UUID driverUuid, final String userUuid) {
        final Driver toAnonymize =
            driverRepository.findDriverAndAttributesBy(driverUuid).orElseThrow(DriverNotFoundException::new);

        return driverAnonymizationRepository
                .save(DriverAnonymization.of(UUID.randomUUID(), toAnonymize.getUuid(), userUuid))
                .getUuid();
    }

    public DriverAttribute saveDriverAttribute(final UUID driverUuid, final DriverAttributeName name,
                                               final String value) {
        return driverRepository.saveDriverAttribute(driverUuid, name, value);
    }

    @Transactional
    public void deleteDriverAttributeBy(final UUID driverUuid, final DriverAttributeName name) {
        driverRepository.deleteDriverAttributeBy(driverUuid, name);
    }

    public void deleteBy(final UUID uuid) {
        driverRepository.deleteBy(uuid);
    }

    @Transactional(readOnly = true)
    public Optional<DriverFileAttribute> findDriverFileAttribute(final UUID driverUuid,
                                                                 final DriverAttributeName driverAttributeName) {
        if (!driverAttributeName.isFile()) {
            throw new DriverAccountException(
                String.format("Driver attribute %s is not of file type", driverAttributeName));
        }
        return driverRepository.findDriverAttribute(driverUuid, driverAttributeName)
                .filter(attr -> attr.getValue().isPresent())
                .map(this::toDriverFileAttribute);
    }

    private DriverFileAttribute toDriverFileAttribute(final DriverAttribute driverAttribute) {
        final String filePath = driverAttribute.getValue().orElseThrow(IllegalArgumentException::new);
        final String url = driverFileService.getUrl(filePath).toString();
        final String fileName = driverFileService.getFileName(filePath);
        return DriverFileAttribute.of(driverAttribute.getName(), url, fileName);
    }

    public boolean existsById(final UUID driverUuid) {
        return driverRepository.existsById(driverUuid);
    }
}

