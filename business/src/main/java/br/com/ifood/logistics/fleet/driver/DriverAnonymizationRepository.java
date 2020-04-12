package br.com.company.logistics.project.driver;

import java.util.List;
import java.util.UUID;

interface DriverAnonymizationRepository {
    
    DriverAnonymization save(DriverAnonymization driverAnonymization);

    List<DriverAnonymization> findAvailableToAnonymize();

    DriverAnonymization findMandatoryByUuid(UUID uuid);

    DriverAnonymization findMandatoryByDriverUuid(UUID driverUuid);

}
