package br.com.company.logistics.project.driver.handler;

import static br.com.company.logistics.project.driver.DriverAttributeName.WORKER_PHOTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.event.AbstractMessageHandler;
import com.company.event.exceptions.InvalidMessageException;
import com.company.event.exceptions.RecoverableException;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import br.com.company.logistics.project.common.DriverAccountException;
import br.com.company.logistics.project.driver.Driver;
import br.com.company.logistics.project.driver.DriverService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class DriverAccountCreateOrUpdateHandler
        extends AbstractMessageHandler<String, DriverAccountCreateOrUpdateMessage> {

    private final ObjectMapper objectMapper;
    private final DriverService driverService;
    private final ConversionService conversionService;

    @Override
    public DriverAccountCreateOrUpdateMessage verifyAndConvert(final String rawMessage) throws InvalidMessageException {
        LOGGER.debug("Verifying and converting, message: {}", rawMessage);
        try {
            return objectMapper.readValue(rawMessage, DriverAccountCreateOrUpdateMessage.class);
        } catch (final IOException e) {
            throw new InvalidMessageException("Couldn't parse message: " + rawMessage, e);
        }
    }

    @Override
    public void process(final DriverAccountCreateOrUpdateMessage message) throws RecoverableException {
        LOGGER.debug("Processing message: {}", message);
        try {
            final Driver driver = toDriver(message);
            if (driverService.existsById(driver.getUuid())) {
                updateWorkerPhoto(driver);
            } else {
                create(driver);
            }
        } catch (final Exception e) {
            throw new RecoverableException("Error on process create or update Driver, message: " + message, e);
        }
    }

    private Driver toDriver(final DriverAccountCreateOrUpdateMessage message) {
        return Optional.ofNullable(conversionService.convert(message, Driver.class))
                .orElseThrow(() -> new DriverAccountException(
                    "Cannot convert Driver from DriverAccountCreateOrUpdateMessage, message: " + message));
    }

    private void create(final Driver newDriver) {
        LOGGER.debug("Creating Driver {}", newDriver.getUuid());
        driverService.save(newDriver);
    }

    private void updateWorkerPhoto(final Driver updatedDriver) {
        LOGGER.debug("Updating Driver {}", updatedDriver.getUuid());
        updatedDriver.getAttributes()
                .stream()
                .filter(a -> WORKER_PHOTO.equals(a.getName()))
                .findFirst()
                .ifPresent(attribute -> driverService.saveDriverAttribute(updatedDriver.getUuid(),
                    attribute.getName(),
                    attribute.getValue().orElse(null)));
    }

    @Override
    public void notifyRetryLimitReachedError(final String message, final Exception e) {
        LOGGER.error("Retry limit reached because of {} \nMessage: {}", e.getMessage(), message);
    }
}
