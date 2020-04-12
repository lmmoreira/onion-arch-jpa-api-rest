package br.com.company.logistics.project.driver;

import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZonedDateTime;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DriverAnonymizationServiceImpl implements DriverAnonymizationService {

    private final DriverFileService driverFileService;
    private final DriverRepository driverRepository;
    private final DriverAnonymizationRepository driverAnonymizationRepository;
    private final IdentityService identityService;

    @Value("${anonymization.enabled:true}")
    private boolean anonymizationEnabled;

    @Scheduled(cron = "${anonymization.interval.cron}")
    @SchedulerLock(name = "DriverAnonymizationService_anonymizeTask",
                   lockAtLeastFor = "${anonymization.interval.lock.least}",
                   lockAtMostFor = "${anonymization.interval.lock.most}")
    public void anonymizeScheduler() {
        LockAssert.assertLocked();

        anonymizeScheduled();
    }

    @Override
    public void anonymizeScheduled() {
        if (anonymizationEnabled) {
            driverAnonymizationRepository.findAvailableToAnonymize().forEach(this::anonymize);
        }
    }

    @Transactional
    public void anonymize(final DriverAnonymization driverAnonymization) {
        final Driver driver = driverRepository.findDriverAndAttributesBy(driverAnonymization.getDriverUuid()).orElseThrow(DriverNotFoundException::new);
        deleteFiles(driver);
        anonymizeDriver(driver);
        updateAnonymization(driverAnonymization);
        identityService.deleteUser(driver.getUserUuid());
    }

    private void anonymizeDriver(final Driver driver) {
        driverRepository.save(Driver.of(driver.getUuid(),
            driver.getExternalId(),
            driver.getTenant(),
            driver.getDeliveryExternalSystem(),
            driver.getExternalUpdatedAt(),
            ZonedDateTime.now(),
            driver.getUserUuid(),
            new HashSet<>()));
    }

    private void updateAnonymization(final DriverAnonymization driverAnonymization) {
        driverAnonymizationRepository.save(DriverAnonymization.of(driverAnonymization.getUuid(),
            driverAnonymization.getDriverUuid(),
            driverAnonymization.getUserUuid(),
            driverAnonymization.getCreatedAt(),
            ZonedDateTime.now()));
    }

    private void deleteFiles(final Driver driver) {
        driver.getAttributes().forEach(this::deleteFile);
    }

    private void deleteFile(final DriverAttribute attribute) {
        if (attribute.getName().isFile()) {
            driverFileService
                    .deleteFile(attribute.getValue().orElseThrow(DriverAnonymizationFileNotFoundException::new));
        }
    }
}

