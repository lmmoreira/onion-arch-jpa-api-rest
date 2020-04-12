package br.com.company.logistics.project.driver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
class DriverAnonymizationRepositoryImpl implements DriverAnonymizationRepository {

    @NonNull
    private final DriverAnonymizationEntityRepository driverAnonymizationEntityRepository;
    @NonNull
    private final ConversionService conversionService;

    @Value("${anonymization.ttl.days:0}")
    private Integer ttlDays;

    @Override
    public DriverAnonymization save(final DriverAnonymization driverAnonymization) {
        final DriverAnonymizationEntity driverAnonymizationEntity = toDriverAnonymizationEntity(driverAnonymization);
        return toDriverAnonymization(driverAnonymizationEntityRepository.save(driverAnonymizationEntity));
    }

    @Override
    public List<DriverAnonymization> findAvailableToAnonymize() {
        final ZonedDateTime availableAnonymizeDate = ZonedDateTime.now().minusDays(ttlDays);

        return driverAnonymizationEntityRepository.findByCreatedAtLessThanAndAnonymizedAtIsNull(availableAnonymizeDate)
                .stream()
                .map(this::toDriverAnonymization)
                .collect(Collectors.toList());
    }

    @Override
    public DriverAnonymization findMandatoryByUuid(final UUID uuid) {
        return toDriverAnonymization(driverAnonymizationEntityRepository.findById(uuid).orElseThrow(DriverAnonymizationNotFoundException::new));
    }

    @Override
    public DriverAnonymization findMandatoryByDriverUuid(final UUID driverUuid) {
        return toDriverAnonymization(driverAnonymizationEntityRepository.findByDriverUuid(driverUuid).orElseThrow(DriverAnonymizationNotFoundException::new));
    }

    private DriverAnonymization toDriverAnonymization(final DriverAnonymizationEntity entity) {
        return conversionService.convert(entity, DriverAnonymization.class);
    }

    private DriverAnonymizationEntity toDriverAnonymizationEntity(final DriverAnonymization driverAnonymization) {
        return conversionService.convert(driverAnonymization, DriverAnonymizationEntity.class);
    }

}
