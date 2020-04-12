package br.com.company.logistics.project.driver;

import static br.com.company.logistics.project.driver.DriverAttributeType.FILE_API;
import static br.com.company.logistics.project.driver.DriverAttributeType.FILE_PATH;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.fasterxml.jackson.databind.node.TextNode;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.company.logistics.project.common.DriverUuidInvalidException;
import br.com.company.logistics.project.common.PaginatorResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/logistics/project/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;
    private final ConversionService conversionService;

    @PostMapping
    public ResponseEntity<DriverResponse> create(@RequestBody final DriverRequest driverRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(conversionService.convert(
                    driverService.save(conversionService.convert(driverRequest, Driver.class)),
                    DriverResponse.class));
    }

    @PutMapping("/{driverUuid}")
    public ResponseEntity<DriverResponse> update(@PathVariable("driverUuid") final UUID driverUuid,
                                                 @RequestBody final DriverRequest driverRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(conversionService.convert(
                    driverService.save(
                        conversionService.convert(validDriverRequestUuid(driverUuid, driverRequest), Driver.class)),
                    DriverResponse.class));
    }

    @DeleteMapping("/{driverUuid}")
    public ResponseEntity<UUID> anonymous(@PathVariable("driverUuid") final UUID driverUuid,
                                          @RequestBody final String userUuid) {
        return ResponseEntity.status(HttpStatus.OK).body(driverService.anonymize(driverUuid, userUuid));
    }

    @PostMapping("/{driverUuid}/attributes/{name}")
    public ResponseEntity<DriverAttributeResponse> saveDriverAttribute(@PathVariable("driverUuid") final UUID driverUuid,
                                                                       @PathVariable("name") final DriverAttributeName name,
                                                                       @RequestBody final TextNode value) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(conversionService.convert(driverService.saveDriverAttribute(driverUuid, name, value.asText()),
                    DriverAttributeResponse.class));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{driverUuid}/attributes/{name}")
    public void deleteDriverAttributeBy(@PathVariable("driverUuid") final UUID driverUuid,
                                        @PathVariable("name") final DriverAttributeName name) {
        driverService.deleteDriverAttributeBy(driverUuid, name);
    }

    @GetMapping("/{driverUuid}")
    public ResponseEntity<DriverResponse> findDriverAndAttributesBy(@PathVariable("driverUuid") final UUID driverUuid,
                                                                    @RequestParam(value = "attributesSelection",
                                                                                  required = false, defaultValue = "") final List<String> attributesSelection) {
        final Optional<Driver> driverOpt = driverService.findDriverAndAttributesBy(driverUuid, attributesSelection);
        final DriverAttributeType strategyFileType = chooseStrategy(attributesSelection);
        return driverOpt.map(driver -> conversionService.convert(DriverAggregatorResponse.of(driver, strategyFileType), DriverResponse.class))
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatorResponse<DriverResponse>> findDriversAndAttributesBy(@RequestBody final DriverSearchRequest driverSearchRequest) {
        final DriverAttributeType strategyFileType = chooseStrategy(driverSearchRequest.getAttributesSelection());
        return ResponseEntity.status(HttpStatus.OK)
            .body(PaginatorResponse.of(driverService
                .findDriversAndAttributesBy(driverSearchRequest.getPage(),
                    driverSearchRequest.getSize(),
                    driverSearchRequest.getDriverIds(),
                    driverSearchRequest.getAttributes(),
                    driverSearchRequest.getAttributesSelection(),
                    driverSearchRequest.getDeliveryExternalSystem())
                .map(driver -> conversionService.convert(DriverAggregatorResponse.of(driver, strategyFileType),
                    DriverResponse.class))));
    }

    @GetMapping
    public ResponseEntity<PaginatorResponse<DriverResponse>> findDriversAndAttributesLegacyBy(@RequestParam(value = "page") final int page,
                                                                                              @RequestParam(value = "size") final int size,
                                                                                              @RequestParam(value = "driverIds",
                                                                                                            required = false) final List<UUID> driverIds) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(PaginatorResponse
                        .of(driverService.findDriversAndAttributesBy(page, size, driverIds, emptyMap(), emptyList(), EMPTY)
                                .map(driver -> conversionService.convert(driver, DriverResponse.class))));
    }

    @GetMapping("/{driverUuid}/attributes/{name}/file")
    public ResponseEntity<DriverFileAttributeResponse> findDriverFileAttribute(@PathVariable("driverUuid") final UUID driverUuid,
                                                                               @PathVariable("name") final DriverAttributeName driverAttributeName) {
        return driverService.findDriverFileAttribute(driverUuid, driverAttributeName)
                .map(fileAttribute -> conversionService.convert(fileAttribute, DriverFileAttributeResponse.class))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private DriverRequest validDriverRequestUuid(final UUID driverUuid, final DriverRequest driverRequest) {
        if (isNull(driverRequest.getUuid())) {
            final String message = String.format("Null driverUuid");
            throw new DriverUuidInvalidException(message);
        } else if (!driverRequest.getUuid().equals(driverUuid)) {
            final String message =
                String.format("Incompatible DriverRequestUuid %s, driverUuid %s", driverRequest.getUuid(), driverUuid);
            throw new DriverUuidInvalidException(message);
        }
        return driverRequest;
    }

    private DriverAttributeType chooseStrategy(final List<String> attributesSelection) {
        return isEmpty(attributesSelection) ? FILE_API : FILE_PATH;
    }

}
