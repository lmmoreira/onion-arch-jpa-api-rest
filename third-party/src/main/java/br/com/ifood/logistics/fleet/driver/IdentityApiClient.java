package br.com.company.logistics.project.driver;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.UUID;

@Headers({"Content-Type: application/json", "Accept: application/json"})
public interface IdentityApiClient {
    @RequestLine("DELETE /v1/users/{userUuid}")
    void deleteUser(@Param("userUuid") UUID userUuid);
}
