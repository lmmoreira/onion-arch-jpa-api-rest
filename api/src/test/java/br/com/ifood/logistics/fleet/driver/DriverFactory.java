package br.com.company.logistics.project.driver;

import com.github.javafaker.Faker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
public class DriverFactory {

    private final static String FILE_PATH = "logistics-data.company-devel.com.br/WORKER_PHOTO/photo.jpg";

    private final Faker faker = new Faker();
    @Autowired
    private DriverRepository driverRepository;

    public void createDriverUp(final int driverQuantity) {
        IntStream.range(1, driverQuantity).forEach(this::createDriver);
    }

    public Driver createDriver(final int iteratorDriver) {
        final Set<DriverAttribute> attributes =
            Set.of(DriverAttribute.of(DriverAttributeName.FATHERS_NAME, faker.name().name()),
                DriverAttribute.of(DriverAttributeName.MOTHERS_NAME, faker.name().name()),
                DriverAttribute.of(DriverAttributeName.EMAIL, faker.internet().emailAddress()),
                DriverAttribute.of(DriverAttributeName.WORKER_PHOTO, FILE_PATH),
                DriverAttribute.of(DriverAttributeName.FULL_NAME, faker.name().fullName()),
                DriverAttribute.of(DriverAttributeName.PHONE, faker.phoneNumber().cellPhone()));

        final Driver driver = Driver.of(UUID.randomUUID(),
            Objects.toString(iteratorDriver),
            "br",
            "project",
            ZonedDateTime.now(),
            UUID.randomUUID(),
            attributes);
        return driverRepository.save(driver);
    }

    public Driver createDriver() {
        return createDriver(new Random().nextInt());
    }
    
    public Driver createDriverWithBankAccount() {
        final Set<DriverAttribute> attributes =
            Set.of(DriverAttribute.of(DriverAttributeName.FATHERS_NAME, faker.name().name()),
                DriverAttribute.of(DriverAttributeName.MOTHERS_NAME, faker.name().name()),
                DriverAttribute.of(DriverAttributeName.EMAIL, faker.internet().emailAddress()),
                DriverAttribute.of(DriverAttributeName.WORKER_PHOTO, FILE_PATH),
                DriverAttribute.of(DriverAttributeName.BANK_ACCOUNT_BANK_CODE, faker.number().digits(3)),
                DriverAttribute.of(DriverAttributeName.BANK_ACCOUNT_BANK_NAME, faker.name().name()),
                DriverAttribute.of(DriverAttributeName.BANK_ACCOUNT_AGENCY, faker.number().digits(4)),
                DriverAttribute.of(DriverAttributeName.BANK_ACCOUNT_NUMBER, faker.number().digits(10)),
                DriverAttribute.of(DriverAttributeName.BANK_ACCOUNT_DIGIT, faker.number().digits(2)));
        final Driver driver = Driver.of(UUID.randomUUID(),
            Objects.toString(new Random().nextLong()),
            "br",
            "project",
            ZonedDateTime.now(),
            UUID.randomUUID(),
            attributes);
        return driverRepository.save(driver);
    }
}
