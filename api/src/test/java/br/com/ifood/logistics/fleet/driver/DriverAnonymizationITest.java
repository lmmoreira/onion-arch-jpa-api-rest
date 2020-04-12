package br.com.company.logistics.project.driver;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import br.com.company.logistics.project.driver.integration.AbstractIntegrationTest;
import lombok.SneakyThrows;

@AutoConfigureMockMvc
@TestInstance(PER_CLASS)
public class DriverAnonymizationITest extends AbstractIntegrationTest {

    private final Faker faker = new Faker();

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverAnonymizationRepository driverAnonymizationRepository;

    @Autowired
    private DriverAnonymizationService driverAnonymizationService;

    @Autowired
    private DriverFactory driverFactory;

    @Autowired
    private DriverService driverService;

    @Test
    @SneakyThrows
    public void testAnonymizeScheduledDriver() {
        final String user = Faker.instance().name().firstName();

        final Driver driver1 = driverFactory.createDriver();
        final Driver driver2 = driverFactory.createDriver();

        driverAnonymizationRepository.save(DriverAnonymization.of(UUID.randomUUID(), driver1.getUuid(), user));
        driverAnonymizationRepository.save(DriverAnonymization.of(UUID.randomUUID(), driver2.getUuid(), user));

        Assert.assertTrue(Objects.isNull(driverService.findDriverAndAttributesBy(driver1.getUuid(), List.of()).get().getAnonymizedAt()));
        Assert.assertTrue(Objects.isNull(driverService.findDriverAndAttributesBy(driver2.getUuid(), List.of()).get().getAnonymizedAt()));

        driverAnonymizationService.anonymizeScheduled();

        Assert.assertTrue(Objects.nonNull(driverService.findDriverAndAttributesBy(driver1.getUuid(), List.of()).get().getAnonymizedAt()));
        Assert.assertTrue(Objects.nonNull(driverService.findDriverAndAttributesBy(driver2.getUuid(), List.of()).get().getAnonymizedAt()));
    }

    @Test
    @SneakyThrows
    public void testAnonymizeScheduledTTLDriver() {
        final Driver driver1 = driverFactory.createDriver();
        final Driver driver2 = driverFactory.createDriver();

        final String user = Faker.instance().name().firstName();

        driverAnonymizationRepository.save(DriverAnonymization.of(UUID.randomUUID(), driver1.getUuid(), user));
        driverAnonymizationRepository.save(DriverAnonymization.of(UUID.randomUUID(), driver2.getUuid(), user));

        Assert.assertTrue(Objects.isNull(driverService.findDriverAndAttributesBy(driver1.getUuid(), List.of()).get().getAnonymizedAt()));
        Assert.assertTrue(Objects.isNull(driverService.findDriverAndAttributesBy(driver2.getUuid(), List.of()).get().getAnonymizedAt()));

        ReflectionTestUtils.setField(driverAnonymizationRepository, "ttlDays", 1);

        driverAnonymizationService.anonymizeScheduled();

        Assert.assertTrue(Objects.isNull(driverService.findDriverAndAttributesBy(driver1.getUuid(), List.of()).get().getAnonymizedAt()));
        Assert.assertTrue(Objects.isNull(driverService.findDriverAndAttributesBy(driver2.getUuid(), List.of()).get().getAnonymizedAt()));

        ReflectionTestUtils.setField(driverAnonymizationRepository, "ttlDays", 0);

        driverAnonymizationService.anonymizeScheduled();

        Assert.assertTrue(Objects.nonNull(driverService.findDriverAndAttributesBy(driver1.getUuid(), List.of()).get().getAnonymizedAt()));
        Assert.assertTrue(Objects.nonNull(driverService.findDriverAndAttributesBy(driver2.getUuid(), List.of()).get().getAnonymizedAt()));
    }

}
