package br.com.company.logistics.project.driver;

import br.com.company.logistics.project.common.CannotDeleteDriverIdentityException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Log4j2
@RequiredArgsConstructor

@Service
public class IdentityServiceImpl implements IdentityService {

    private final IdentityApiClient identityApiClient;

    @Override
    public void deleteUser(final UUID userUuid) {
        try {
            if (Objects.nonNull(userUuid)) {
                identityApiClient.deleteUser(userUuid);
            } else {
                final String message = "Cannot delete user at Identity because userUuid is null.";
                log.error(message);
                throw new CannotDeleteDriverIdentityException(message);
            }
        } catch (final FeignException e) {
            final String message = "Cannot delete user at Identity, userUuid: " + userUuid;
            log.error(message, e);
            throw new CannotDeleteDriverIdentityException(message, e);
        }
    }
}
