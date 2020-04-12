package br.com.company.logistics.project.driver;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository repository;

    @InjectMocks
    private DriverService service;

    @Test
    void shouldSaveWhenNewDriver() {
        final UUID driverUuid = UUID.randomUUID();
        final UUID userUuid = UUID.randomUUID();
        final Driver driver =
            Driver.of(driverUuid, "1", "br", "project", ZonedDateTime.now(), userUuid, Collections.emptySet());
        service.save(driver);
        verify(repository, times(1)).save(eq(driver));
    }
}
