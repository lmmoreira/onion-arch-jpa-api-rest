package br.com.company.logistics.project.driver;

import static br.com.company.logistics.project.driver.DriverAttributeName.CPF;
import static br.com.company.logistics.project.driver.DriverAttributeName.DRIVERS_LICENSE;
import static br.com.company.logistics.project.driver.DriverAttributeName.DRIVERS_LICENSE_PHOTO;
import static br.com.company.logistics.project.driver.DriverAttributeName.EMAIL;
import static br.com.company.logistics.project.driver.DriverAttributeName.FATHERS_NAME;
import static br.com.company.logistics.project.driver.DriverAttributeName.FULL_NAME;
import static br.com.company.logistics.project.driver.DriverAttributeName.MOTHERS_NAME;
import static br.com.company.logistics.project.driver.DriverAttributeName.PHONE;
import static br.com.company.logistics.project.driver.DriverAttributeName.WORKER_PHOTO;
import static br.com.company.logistics.project.driver.DriverAttributeType.FILE_API;
import static br.com.company.logistics.project.driver.DriverAttributeType.FILE_NAME;
import static br.com.company.logistics.project.driver.DriverAttributeType.FILE_PATH;
import static br.com.company.logistics.project.driver.DriverAttributeType.TEXT;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.JsonPath;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import br.com.company.logistics.project.driver.integration.AbstractIntegrationTest;
import lombok.SneakyThrows;

@AutoConfigureMockMvc
@TestInstance(PER_CLASS)
public class DriverControllerITest extends AbstractIntegrationTest {

    private static final String ROUTE_BASE_DRIVER = "/api/logistics/project/drivers";
    private static final String DELIVERY_EXTERNAL_SYSTEM_project_CO = "project-co";
    private static final String TENANT_CO = "co";

    private final Faker faker = new Faker();

    private final ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private MockMvc mvc;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverAnonymizationRepository driverAnonymizationRepository;

    @Autowired
    private DriverFactory driverFactory;

    @Autowired
    private DriverService driverService;

    @Autowired
    private DriverAnonymizationService driverAnonymizationService;

    @MockBean
    private IdentityService mockedIdentityService;

    @BeforeAll
    private void beforeAll() {
        driverFactory.createDriverUp(10);
    }

    @Test
    public void testPostDriver() throws Exception {
        final String content = new DriverJsonBuilder().build();
        final MvcResult mvcResult =
                mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(content))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.uuid").exists())
                        .andReturn();
        final String uuid = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.uuid");
        assertThat(driverRepository.findDriverAndAttributesBy(UUID.fromString(uuid)).isPresent()).isTrue();
    }

    @Test
    public void testPostDriverWhenDeliveryExternalSystemAndExternalIdAreEmpty() throws Exception {
        final String content =
            new DriverJsonBuilder().externalId(null).build();
        final MvcResult mvcResult =
            mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(content))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.uuid").exists())
                    .andReturn();
        final String uuid = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.uuid");
        assertThat(driverRepository.findDriverAndAttributesBy(UUID.fromString(uuid)).isPresent()).isTrue();
    }

    @Test
    public void testPostDriverWithAttributeUnknown() throws Exception {
        final String content =
                new DriverJsonBuilder().addAttribute(new DriverAttributeRequest("ALTURA", "1.75"))
                        .build();
        final MvcResult mvcResult =
                mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(content))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.uuid").exists())
                        .andReturn();
        final String uuid = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.uuid");
        assertThat(driverRepository.findDriverAndAttributesBy(UUID.fromString(uuid)).isPresent()).isTrue();
    }

    @Test
    public void testPostDriverWithTenantIsUS() throws Exception {
        final String content = new DriverJsonBuilder().tenant("Us").build();
        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tenant").value("br"));
    }

    @Test
    public void testGetDriversSchema() throws Exception {
        final var page = "2";
        final var size = "3";
        mvc.perform(
                get(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).param("page", page).param("size", size))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].uuid").exists())
                .andExpect(jsonPath("$.content.[0].externalId").exists())
                .andExpect(jsonPath("$.content.[0].tenant").exists())
                .andExpect(jsonPath("$.content.[0].deliveryExternalSystem").exists())
                .andExpect(jsonPath("$.content.[0].externalUpdatedAt").exists())
                .andExpect(jsonPath("$.content.[0].attributes").exists())
                .andExpect(jsonPath("$.pageable.page").value(page))
                .andExpect(jsonPath("$.pageable.size").value(size))
                .andExpect(jsonPath("$.total").exists());
    }

    @Test
    @SneakyThrows
    public void testGetDriver() {
        final Driver driver = driverFactory.createDriver();
        mvc.perform(get(ROUTE_BASE_DRIVER + "/{driverUuid}", driver.getUuid()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(7)))
                .andExpect(jsonPath("$.uuid").value(driver.getUuid().toString()))
                .andExpect(jsonPath("$.externalId").value(driver.getExternalId()))
                .andExpect(jsonPath("$.tenant").value(driver.getTenant()))
                .andExpect(jsonPath("$.deliveryExternalSystem").value(driver.getDeliveryExternalSystem()))
                .andExpect(jsonPath("$.userUuid").value(driver.getUserUuid().toString()))
                .andExpect(jsonPath("$.attributes", hasSize(driver.getAttributes().size())))
                .andExpect(jsonPath("$.attributes[*].type", hasItems(FILE_API.name(), TEXT.name())));
    }

    @Test
    @SneakyThrows
    public void testGetDriverWhenFilterByAttributeSelection() {
        final Driver driver = driverFactory.createDriver();
        mvc.perform(get(ROUTE_BASE_DRIVER + "/{driverUuid}", driver.getUuid()).contentType(MediaType.APPLICATION_JSON)
                .param("attributesSelection", "FATHERS_NAME, MOTHERS_NAME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(7)))
                .andExpect(jsonPath("$.uuid").value(driver.getUuid().toString()))
                .andExpect(jsonPath("$.externalId").value(driver.getExternalId()))
                .andExpect(jsonPath("$.tenant").value(driver.getTenant()))
                .andExpect(jsonPath("$.deliveryExternalSystem").value(driver.getDeliveryExternalSystem()))
                .andExpect(jsonPath("$.userUuid").value(driver.getUserUuid().toString()))
                .andExpect(jsonPath("$.attributes", hasSize(is(2))))
                .andExpect(jsonPath("$.attributes[*].name", hasItems(FATHERS_NAME.name(), MOTHERS_NAME.name())));
    }

    @Test
    @SneakyThrows
    public void testGetDriverWhenAttributeNotExist() {
        final UUID driverId = UUID.randomUUID();
        final String driverLicensePhoto =
                "logistics-data.company.com.br/attributes/WORKER/id-12450/DRIVERS_LICENSE_PHOTO/license_foto.jpg";
        final String urlDriverLicense = "http://" + faker.internet().url();
        final String driverContent = new DriverJsonBuilder().uuid(driverId)
                .addAttribute(new DriverAttributeRequest(FULL_NAME.name(), faker.name().fullName()))
                .addAttribute(new DriverAttributeRequest(FATHERS_NAME.name(), faker.name().fullName()))
                .addAttribute(new DriverAttributeRequest(DRIVERS_LICENSE_PHOTO.name(), driverLicensePhoto))
                .build();

        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(driverContent));

        when(fileRepositoryService.getUrl(eq("logistics-data.company.com.br"),
                eq("attributes/WORKER/id-12450/DRIVERS_LICENSE_PHOTO/license_foto.jpg"),
                anyLong(),
                anyBoolean())).thenReturn(new URL(urlDriverLicense));

        mvc.perform(
                get(ROUTE_BASE_DRIVER + "/" + driverId).param("attributesSelection", "MOTHERS_NAME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(driverId.toString()))
                .andExpect(jsonPath("$.attributes", hasSize(is(0))));
    }

    @Test
    @SneakyThrows
    public void testGetDriverWhenFilterByAttributeFile() {
        final UUID driverId = UUID.randomUUID();
        final String photo = "logistics-data.company.com.br/attributes/WORKER/id-12450/WORKER_PHOTO/foto.jpg";
        final String driverLicensePhoto =
            "logistics-data.company.com.br/attributes/WORKER/id-12450/DRIVERS_LICENSE_PHOTO/license_foto.jpg";
        final String urlWorkerPhoto = "http://" + faker.internet().url();
        final String urlDriverLicense = "http://" + faker.internet().url();
        final String driverContent = new DriverJsonBuilder().uuid(driverId)
                .addAttribute(new DriverAttributeRequest(WORKER_PHOTO.name(), photo))
                .addAttribute(new DriverAttributeRequest(DRIVERS_LICENSE_PHOTO.name(), driverLicensePhoto))
                .build();

        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(driverContent));

        when(fileRepositoryService.getUrl(eq("logistics-data.company.com.br"),
            eq("attributes/WORKER/id-12450/WORKER_PHOTO/foto.jpg"),
            anyLong(),
            anyBoolean())).thenReturn(new URL(urlWorkerPhoto));

        when(fileRepositoryService.getUrl(eq("logistics-data.company.com.br"),
            eq("attributes/WORKER/id-12450/DRIVERS_LICENSE_PHOTO/license_foto.jpg"),
            anyLong(),
            anyBoolean())).thenReturn(new URL(urlDriverLicense));

        mvc.perform(
            get(ROUTE_BASE_DRIVER + "/" + driverId).param("attributesSelection", "WORKER_PHOTO, DRIVERS_LICENSE_PHOTO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(driverId.toString()))
                .andExpect(jsonPath("$.attributes", hasSize(is(2))))
                .andExpect(jsonPath("$.attributes[*].type", hasItems(FILE_PATH.name())))
                .andExpect(jsonPath("$.attributes[*].value", hasItems(urlWorkerPhoto, urlDriverLicense)));

    }
    
    @Test
    public void testPutDriver() throws Exception {
        final Driver driver = driverFactory.createDriver();
        final Set<DriverAttributeRequest> attributes =
                Set.of(new DriverAttributeRequest(EMAIL.name(), faker.internet().emailAddress()));
        final String content = new DriverJsonBuilder().uuid(driver.getUuid()).attributes(attributes).build();
        mvc.perform(put(ROUTE_BASE_DRIVER + "/{driverUuid}", driver.getUuid()).contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldNotDeleteBankAccountAttributesWhenNotPresent() throws Exception {
        final Driver driver = driverFactory.createDriverWithBankAccount();
        final Set<DriverAttributeRequest> attributes =
                Set.of(new DriverAttributeRequest(EMAIL.name(), faker.internet().emailAddress()));
        final String content = new DriverJsonBuilder().uuid(driver.getUuid()).attributes(attributes).build();
        mvc.perform(put(ROUTE_BASE_DRIVER + "/{driverUuid}", driver.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());

        final Driver actualDriver = driverService.findDriverAndAttributesBy(driver.getUuid(), List.of())
                .orElseThrow(() -> new AssertionError("No driver"));
        final int expectedSize = attributes.size() + DriverAttributeName.valuesByBankAccountGroup().size();
        assertThat(expectedSize).isEqualTo(actualDriver.getAttributes().size());
    }

    @Test
    @SneakyThrows
    public void testAnonymizeDriver() {
        final Driver driver = driverFactory.createDriver();
        final String userUuid = UUID.randomUUID().toString();

        mvc.perform(delete(ROUTE_BASE_DRIVER + "/{driverUuid}", driver.getUuid())
            .contentType(MediaType.APPLICATION_JSON)
            .content(userUuid))
            .andExpect(status().isOk());

        final List<DriverAnonymization> anonymizations = driverAnonymizationRepository.findAvailableToAnonymize();
        Assert.assertFalse(anonymizations.isEmpty());
        Assert.assertEquals(anonymizations.get(0).getUserUuid(), userUuid);
        Assert.assertEquals(anonymizations.get(0).getDriverUuid(), driver.getUuid());
    }

    @Test
    @SneakyThrows
    public void testAnonymizeDriverNotFound() {
        final String userUuid = UUID.randomUUID().toString();

        mvc.perform(delete(ROUTE_BASE_DRIVER + "/{driverUuid}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(userUuid))
            .andExpect(status().isInternalServerError());
    }

    @Test
    public void testUpdatedAtWhenAttributeValueIsNotChanged() throws Exception {
        final Driver driver = driverFactory.createDriver();

        final DriverAttribute emailBefore = driver.getAttributes().stream().filter(att -> EMAIL.equals(att.getName())).findFirst().orElse(null);
        final Set<DriverAttributeRequest> attributes =
                Set.of(new DriverAttributeRequest(EMAIL.name(), emailBefore.getValue().orElse(null)));
        final String content = new DriverJsonBuilder().uuid(driver.getUuid()).attributes(attributes).build();

        mvc.perform(put(ROUTE_BASE_DRIVER + "/{driverUuid}", driver.getUuid()).contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());
        
        final Driver reloadedDriver = driverService.findDriverAndAttributesBy(driver.getUuid(), List.of()).get();
        final DriverAttribute emailAfter = reloadedDriver.getAttributes().stream().filter(att -> EMAIL.equals(att.getName())).findFirst().orElse(null);
        Assert.assertEquals(emailBefore.getUpdatedAt(), emailAfter.getUpdatedAt());
    }

    @Test
    public void testUpdatedAtWhenAttributeValueIsChanged() throws Exception {
        final Driver driver = driverFactory.createDriver();

        final DriverAttribute emailBefore = driver.getAttributes().stream().filter(att -> EMAIL.equals(att.getName())).findFirst().orElse(null);
        final Set<DriverAttributeRequest> attributes =
                Set.of(new DriverAttributeRequest(EMAIL.name(), "test@company.com.br"));
        final String content = new DriverJsonBuilder().uuid(driver.getUuid()).attributes(attributes).build();

        mvc.perform(put(ROUTE_BASE_DRIVER + "/{driverUuid}", driver.getUuid()).contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());


        final Driver reloadedDriver = driverService.findDriverAndAttributesBy(driver.getUuid(), List.of()).get();
        final DriverAttribute emailAfter = reloadedDriver.getAttributes().stream().filter(att -> EMAIL.equals(att.getName())).findFirst().orElse(null);
        Assert.assertNotEquals(emailBefore.getUpdatedAt(), emailAfter.getUpdatedAt());
    }

    @Test
    public void testPutDriverWhenDriverUuidIsNullInDriverRequest() throws Exception {
        final Driver driver = driverFactory.createDriver();
        final Set<DriverAttributeRequest> attributes =
                Set.of(new DriverAttributeRequest(EMAIL.name(), faker.internet().emailAddress()));
        final String content = new DriverJsonBuilder().uuid(null).attributes(attributes).build();
        mvc.perform(put(ROUTE_BASE_DRIVER + "/{driverUuid}", driver.getUuid()).contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPostDriverAttribute() throws Exception {
        final Driver driver = driverFactory.createDriver();
        final String expectedValue =
            "logistics-data.company-devel.com.br/attribute-key-IDENTITY_DOCUMENT_FRONT_PHOTO/foto-rosto.jpeg";
        final String content = "\"" + expectedValue + "\"";
        mvc.perform(post(ROUTE_BASE_DRIVER + "/{driverUuid}/attributes/{name}", driver.getUuid(), WORKER_PHOTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.value").value(expectedValue));
    }

    @Test
    public void testDeleteDriverAttribute() throws Exception {
        final Driver driver = driverFactory.createDriver();
        
        assertThat(driver.getAttributes()).extracting(DriverAttribute::getName).contains(WORKER_PHOTO);

        mvc.perform(delete(ROUTE_BASE_DRIVER + "/{driverUuid}/attributes/{name}", driver.getUuid(), WORKER_PHOTO)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        final Driver driverSaved =
            driverService.findDriverAndAttributesBy(driver.getUuid(), emptyList()).orElseThrow(DriverNotFoundException::new);
        driverSaved.getAttributes()
                .forEach(driverAttribute -> assertThat(driver.getAttributes()).extracting(DriverAttribute::getName)
                        .contains(driverAttribute.getName()));
        assertThat(driverSaved.getAttributes()).extracting(DriverAttribute::getName).doesNotContain(WORKER_PHOTO);
    }

    @Test
    public void shouldGetBadRequestWhenDriverUuidIsIncompatible() throws Exception {
        final UUID driverUuid = UUID.randomUUID();
        final Driver driver = driverFactory.createDriver();
        final Set<DriverAttributeRequest> attributes =
                Set.of(new DriverAttributeRequest(EMAIL.name(), faker.internet().emailAddress()));
        final String content = new DriverJsonBuilder().uuid(driver.getUuid()).attributes(attributes).build();
        final String messageIncompatible = String.format("Incompatible DriverRequestUuid %s, driverUuid %s",
                driver.getUuid(),
                driverUuid);
        mvc.perform(put(ROUTE_BASE_DRIVER + "/{driverUuid}", driverUuid).contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(messageIncompatible));
    }

    @Test
    @SneakyThrows
    public void testFindDriversAndAttributesByDriverIds(){
        final UUID firstUuid = driverFactory.createDriver().getUuid();
        final UUID secondUuid = driverFactory.createDriver().getUuid();
           
        final var page = "0";
        final var size = "1";
        mvc.perform(get(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON)
                .param("page", page)
                .param("size", size)
                .param("driverIds", firstUuid.toString(), secondUuid.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].uuid").exists())
                .andExpect(jsonPath("$.content.[0].externalId").exists())
                .andExpect(jsonPath("$.content.[0].tenant").exists())
                .andExpect(jsonPath("$.content.[0].deliveryExternalSystem").exists())
                .andExpect(jsonPath("$.content.[0].externalUpdatedAt").exists())
                .andExpect(jsonPath("$.content.[0].attributes").exists())
                .andExpect(jsonPath("$.pageable.page").value(page))
                .andExpect(jsonPath("$.pageable.size").value(size))
                .andExpect(jsonPath("$.total").value(2));
    }

    @SneakyThrows
    private String createDriver(final UUID uuid) {
        final String driverJson = new DriverJsonBuilder().uuid(uuid).build();
        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(driverJson));
        return driverJson;
    }
    
    @Test
    @SneakyThrows
    public void testSearchDriversAndAttributesByDriverIdsAndAttributesSelection() {
        final UUID firstUuid = driverFactory.createDriver().getUuid();

        final int page = 0;
        final int size = 2;
        final List<UUID> driversIds = ImmutableList.of(firstUuid);
        final Driver driver = driverService.findDriverAndAttributesBy(firstUuid, List.of()).orElseThrow();

        final String fathersName = driver.getAttributeValue(FATHERS_NAME).orElseThrow(AssertionError::new);
        final String email = driver.getAttributeValue(EMAIL).orElseThrow(AssertionError::new);

        final Map<String, String> attributes = ImmutableMap.of("FATHERS_NAME", fathersName, "EMAIL", email);
        final ImmutableList<String> attributesSelection = ImmutableList.of("FULL_NAME");
        final String content = mapper.writeValueAsString(
            new DriverSearchRequest(page, size, driversIds, attributes, attributesSelection, EMPTY));

        mvc.perform(post(ROUTE_BASE_DRIVER + "/search").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*].uuid", hasItems(firstUuid.toString())))
                .andExpect(jsonPath("$.content.[0].externalId").exists())
                .andExpect(jsonPath("$.content.[0].tenant").exists())
                .andExpect(jsonPath("$.content.[0].deliveryExternalSystem").exists())
                .andExpect(jsonPath("$.content.[0].externalUpdatedAt").exists())
                .andExpect(jsonPath("$.content.[0].attributes").exists())
                .andExpect(jsonPath("$.content[*].attributes[*].name", hasSize(is(1))))
                .andExpect(
        jsonPath("$.content[*].attributes[*].name", hasItems(FULL_NAME.toString())))
                .andExpect(jsonPath("$.pageable.page").value(page))
                .andExpect(jsonPath("$.pageable.size").value(size))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @SneakyThrows
    public void testSearchDriversAndAttributesByAttributeIsPhone() {
        final UUID firstUuid = driverFactory.createDriver().getUuid();

        final int page = 0;
        final int size = 2;
        final Driver driver = driverService.findDriverAndAttributesBy(firstUuid, List.of()).orElseThrow();
        
        final String email = driver.getAttributeValue(EMAIL).orElseThrow(AssertionError::new);
        final String phone = driver.getAttributeValue(PHONE).orElseThrow(AssertionError::new);

        final Map<String, String> attributes = ImmutableMap.of("PHONE", phone, "EMAIL", email);
        final ImmutableList<String> attributesSelection = ImmutableList.of("PHONE");
        final String content = mapper.writeValueAsString(
                new DriverSearchRequest(page, size, emptyList(), attributes, attributesSelection, EMPTY));

        mvc.perform(post(ROUTE_BASE_DRIVER + "/search").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*].uuid", hasItems(firstUuid.toString())))
                .andExpect(jsonPath("$.content.[0].externalId").exists())
                .andExpect(jsonPath("$.content.[0].tenant").exists())
                .andExpect(jsonPath("$.content.[0].deliveryExternalSystem").exists())
                .andExpect(jsonPath("$.content.[0].externalUpdatedAt").exists())
                .andExpect(jsonPath("$.content.[0].attributes").exists())
                .andExpect(jsonPath("$.content[*].attributes[*].name", hasSize(is(1))))
                .andExpect(
                        jsonPath("$.content[*].attributes[*].name", hasItems(PHONE.toString())))
                .andExpect(
                        jsonPath("$.content[*].attributes[*].value").value(phone))
                .andExpect(jsonPath("$.pageable.page").value(page))
                .andExpect(jsonPath("$.pageable.size").value(size))
                .andExpect(jsonPath("$.total").value(1));
        
    }
    
    @Test
    @SneakyThrows
    public void shouldNotFindDriverWhenPhoneIsIncomplete() {
        final UUID firstUuid = driverFactory.createDriver().getUuid();

        final int page = 0;
        final int size = 2;
        final Driver driver = driverService.findDriverAndAttributesBy(firstUuid, List.of()).orElseThrow();

        final String email = driver.getAttributeValue(EMAIL).orElseThrow(AssertionError::new);
        final String phoneIncomplete = driver.getAttributeValue(PHONE).orElseThrow(AssertionError::new).substring(3);

        final Map<String, String> attributes = ImmutableMap.of("PHONE", phoneIncomplete, "EMAIL", email);
        final ImmutableList<String> attributesSelection = ImmutableList.of("PHONE", "EMAIL");
        final String content = mapper.writeValueAsString(
                new DriverSearchRequest(page, size,  emptyList(), attributes, attributesSelection, EMPTY));

        mvc.perform(post(ROUTE_BASE_DRIVER + "/search").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].uuid").doesNotExist())
                .andExpect(jsonPath("$.pageable.page").value(page))
                .andExpect(jsonPath("$.pageable.size").value(size))
                .andExpect(jsonPath("$.total").value(0));

    }

    @Test
    @SneakyThrows
    public void testSearchDriversAndAttributesByDriverIdsWhenAttributeSelectionNotExists() {
        final UUID firstUuid = driverFactory.createDriver().getUuid();

        final int page = 0;
        final int size = 2;
        final List<UUID> driversIds = ImmutableList.of(firstUuid);
        final Driver driver = driverService.findDriverAndAttributesBy(firstUuid, List.of()).orElseThrow();

        final String fathersName = driver.getAttributeValue(FATHERS_NAME).orElseThrow(AssertionError::new);
        final String email = driver.getAttributeValue(EMAIL).orElseThrow(AssertionError::new);

        final Map<String, String> attributes = ImmutableMap.of("FATHERS_NAME", fathersName, "EMAIL", email);
        final ImmutableList<String> attributesSelection = ImmutableList.of("BACKGROUND_CHECK_ID");
        final String content = mapper.writeValueAsString(
            new DriverSearchRequest(page, size, driversIds, attributes, attributesSelection, EMPTY));

        mvc.perform(post(ROUTE_BASE_DRIVER + "/search").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*].uuid", hasItems(firstUuid.toString())))
                .andExpect(jsonPath("$.content.[0].externalId").exists())
                .andExpect(jsonPath("$.content.[0].tenant").exists())
                .andExpect(jsonPath("$.content.[0].deliveryExternalSystem").exists())
                .andExpect(jsonPath("$.content.[0].externalUpdatedAt").exists())
                .andExpect(jsonPath("$.content.[0].attributes").exists())
                .andExpect(jsonPath("$.content[*].attributes[*].name", hasSize(is(0))))
                .andExpect(jsonPath("$.pageable.page").value(page))
                .andExpect(jsonPath("$.pageable.size").value(size))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @SneakyThrows
    public void testSearchDriversAndAttributesByDriverIdsAndAttributesSelectionWithTypeFile() {
        final UUID firstUuid = UUID.randomUUID();
        final UUID secondUuid = UUID.randomUUID();
        final String firstDriver = new DriverJsonBuilder().uuid(firstUuid).build();
        final String url = "http://" + faker.internet().url();
        final String photo = "logistics-data.company.com.br/attributes/WORKER/id-12450/WORKER_PHOTO/foto.jpg";
        final String secondDriver = new DriverJsonBuilder().uuid(secondUuid)
                .addAttribute(new DriverAttributeRequest(WORKER_PHOTO.name(), photo))
                .build();
        final String thirdDriver = new DriverJsonBuilder().uuid(UUID.randomUUID()).build();
        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(firstDriver));
        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(secondDriver));
        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(thirdDriver));

        when(fileRepositoryService.getUrl(eq("logistics-data.company.com.br"),
            eq("attributes/WORKER/id-12450/WORKER_PHOTO/foto.jpg"),
            anyLong(),
            anyBoolean())).thenReturn(new URL(url));

        final int page = 0;
        final int size = 2;
        final List<UUID> driversIds = ImmutableList.of(firstUuid, secondUuid);
        final Driver driver = driverService.findDriverAndAttributesBy(secondUuid, List.of()).orElseThrow();

        final String fathersName = driver.getAttributeValue(FATHERS_NAME).orElseThrow(AssertionError::new);
        final String email = driver.getAttributeValue(EMAIL).orElseThrow(AssertionError::new);

        final Map<String, String> attributes = ImmutableMap.of("FATHERS_NAME", fathersName, "EMAIL", email);
        final ImmutableList<String> attributesSelection = ImmutableList.of("WORKER_PHOTO");
        final String content = mapper.writeValueAsString(
            new DriverSearchRequest(page, size, driversIds, attributes, attributesSelection, EMPTY));

        mvc.perform(post(ROUTE_BASE_DRIVER + "/search").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*].uuid", hasItems(secondUuid.toString())))
                .andExpect(jsonPath("$.content.[0].externalId").exists())
                .andExpect(jsonPath("$.content.[0].tenant").exists())
                .andExpect(jsonPath("$.content.[0].deliveryExternalSystem").exists())
                .andExpect(jsonPath("$.content.[0].externalUpdatedAt").exists())
                .andExpect(jsonPath("$.content.[0].attributes").exists())
                .andExpect(jsonPath("$.content[*].attributes[*].name", hasSize(is(1))))
                .andExpect(jsonPath("$.content[*].attributes[*].name", hasItems(WORKER_PHOTO.toString())))
                .andExpect(jsonPath("$.content[*].attributes[*].type", hasItems(FILE_PATH.name())))
                .andExpect(jsonPath("$.content[*].attributes[*].value", hasItems(url)))
                .andExpect(jsonPath("$.pageable.page").value(page))
                .andExpect(jsonPath("$.pageable.size").value(size))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @SneakyThrows
    public void testSearchDriversAndAttributesByDriverIdsAndAttributesSelectionAndTypeFileName() {
        final UUID firstUuid = UUID.randomUUID();
        final UUID secondUuid = UUID.randomUUID();
        final String firstDriver = new DriverJsonBuilder().uuid(firstUuid).build();
        final String photo = "/foto.jpg";
        final String secondDriver = new DriverJsonBuilder().uuid(secondUuid).deliveryExternalSystem("rapiddo")
                .addAttribute(new DriverAttributeRequest(WORKER_PHOTO.name(), photo))
                .build();
        final String thirdDriver = new DriverJsonBuilder().uuid(UUID.randomUUID()).build();
        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(firstDriver));
        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(secondDriver));
        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(thirdDriver));

        final int page = 0;
        final int size = 2;
        final List<UUID> driversIds = ImmutableList.of(firstUuid, secondUuid);
        final Driver driver = driverService.findDriverAndAttributesBy(secondUuid, List.of()).orElseThrow();

        final String fathersName = driver.getAttributeValue(FATHERS_NAME).orElseThrow(AssertionError::new);
        final String email = driver.getAttributeValue(EMAIL).orElseThrow(AssertionError::new);

        final Map<String, String> attributes = ImmutableMap.of("FATHERS_NAME", fathersName, "EMAIL", email);
        final ImmutableList<String> attributesSelection = ImmutableList.of("WORKER_PHOTO");
        final String content = mapper.writeValueAsString(
                new DriverSearchRequest(page, size, driversIds, attributes, attributesSelection, EMPTY));

        mvc.perform(post(ROUTE_BASE_DRIVER + "/search").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*].uuid", hasItems(secondUuid.toString())))
                .andExpect(jsonPath("$.content.[0].externalId").exists())
                .andExpect(jsonPath("$.content.[0].tenant").exists())
                .andExpect(jsonPath("$.content.[0].deliveryExternalSystem").exists())
                .andExpect(jsonPath("$.content.[0].externalUpdatedAt").exists())
                .andExpect(jsonPath("$.content.[0].attributes").exists())
                .andExpect(jsonPath("$.content[*].attributes[*].name", hasSize(is(1))))
                .andExpect(jsonPath("$.content[*].attributes[*].name", hasItems(WORKER_PHOTO.toString())))
                .andExpect(jsonPath("$.content[*].attributes[*].type", hasItems(FILE_NAME.name())))
                .andExpect(jsonPath("$.content[*].attributes[*].value", hasItems(photo)))
                .andExpect(jsonPath("$.pageable.page").value(page))
                .andExpect(jsonPath("$.pageable.size").value(size))
                .andExpect(jsonPath("$.total").value(1));
    }
    
    @Test
    @SneakyThrows
    public void testSearchDriversAndAttributesByDriverIds() {
        final UUID firstUuid = UUID.randomUUID();
        final UUID secondUuid = UUID.randomUUID();
        final String firstDriver = new DriverJsonBuilder().uuid(firstUuid).build();
        final String secondDriver = new DriverJsonBuilder().uuid(secondUuid).build();
        final String thirdDriver = new DriverJsonBuilder().uuid(UUID.randomUUID()).build();
        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(firstDriver));
        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(secondDriver));
        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(thirdDriver));

        final int page = 0;
        final int size = 2;
        final List<UUID> driversIds = ImmutableList.of(firstUuid);
        final Driver driver = driverService.findDriverAndAttributesBy(firstUuid, List.of()).orElseThrow();

        final String fathersName = driver.getAttributeValue(FATHERS_NAME).orElseThrow(AssertionError::new);
        final String email = driver.getAttributeValue(EMAIL).orElseThrow(AssertionError::new);

        final Map<String, String> attributes = ImmutableMap.of("FATHERS_NAME", fathersName, "EMAIL", email);
        final String content = mapper.writeValueAsString(
            new DriverSearchRequest(page, size, driversIds, attributes, emptyList(), EMPTY));

        mvc.perform(post(ROUTE_BASE_DRIVER + "/search").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*].uuid", hasItems(firstUuid.toString())))
                .andExpect(jsonPath("$.content.[0].externalId").exists())
                .andExpect(jsonPath("$.content.[0].tenant").exists())
                .andExpect(jsonPath("$.content.[0].deliveryExternalSystem").exists())
                .andExpect(jsonPath("$.content.[0].externalUpdatedAt").exists())
                .andExpect(jsonPath("$.content.[0].attributes").exists())
                .andExpect(
                    jsonPath("$.content[*].attributes[*].name", hasItems(FATHERS_NAME.toString(), EMAIL.toString())))
                .andExpect(jsonPath("$.content[*].attributes[*].value", hasItems(fathersName, email)))
                .andExpect(jsonPath("$.content.[0].attributes").exists())
                .andExpect(jsonPath("$.pageable.page").value(page))
                .andExpect(jsonPath("$.pageable.size").value(size))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @SneakyThrows
    public void testSearchDriversWhenParametersIsEmpty() {
        final int page = 0;
        final int size = 2;
        final String content = mapper.writeValueAsString(
            new DriverSearchRequest(page, size, emptyList(), emptyMap(), emptyList(), EMPTY));
        mvc.perform(post(ROUTE_BASE_DRIVER + "/search").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].uuid").exists())
                .andExpect(jsonPath("$.content.[0].externalId").exists())
                .andExpect(jsonPath("$.content.[0].tenant").exists())
                .andExpect(jsonPath("$.content.[0].deliveryExternalSystem").exists())
                .andExpect(jsonPath("$.content.[0].externalUpdatedAt").exists())
                .andExpect(jsonPath("$.content.[0].attributes").exists())
                .andExpect(jsonPath("$.pageable.page").value(page))
                .andExpect(jsonPath("$.pageable.size").value(size))
                .andExpect(jsonPath("$.total").value(9));
    }

    @Test
    @SneakyThrows
    public void testSearchDriversWhenDeliveryExternalSystemIsprojectCO() {
        final int page = 0;
        final int size = 2;
        final String driver = new DriverJsonBuilder().uuid(UUID.randomUUID())
                .deliveryExternalSystem(DELIVERY_EXTERNAL_SYSTEM_project_CO)
                .tenant(TENANT_CO)
                .build();
        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(driver));
        final String content = mapper.writeValueAsString(new DriverSearchRequest(page, size, emptyList(), emptyMap(),
            emptyList(), DELIVERY_EXTERNAL_SYSTEM_project_CO));
        mvc.perform(post(ROUTE_BASE_DRIVER + "/search").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].uuid").exists())
                .andExpect(jsonPath("$.content.[0].externalId").exists())
                .andExpect(jsonPath("$.content.[0].tenant").value(TENANT_CO))
                .andExpect(jsonPath("$.content.[0].deliveryExternalSystem").value(DELIVERY_EXTERNAL_SYSTEM_project_CO))
                .andExpect(jsonPath("$.content.[0].externalUpdatedAt").exists())
                .andExpect(jsonPath("$.content.[0].attributes").exists())
                .andExpect(jsonPath("$.pageable.page").value(page))
                .andExpect(jsonPath("$.pageable.size").value(size))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @SneakyThrows
    public void testSearchDriversWhenDeliveryExternalSystemIsUnknown() {
        final int page = 0;
        final int size = 2;
        final String driver = new DriverJsonBuilder().uuid(UUID.randomUUID())
                .deliveryExternalSystem("unknown")
                .build();
        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(driver));
        final String content = mapper.writeValueAsString(new DriverSearchRequest(page, size, emptyList(), emptyMap(),
                emptyList(), DELIVERY_EXTERNAL_SYSTEM_project_CO));
        mvc.perform(post(ROUTE_BASE_DRIVER + "/search").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].uuid").doesNotExist())
                .andExpect(jsonPath("$.pageable.page").value(page))
                .andExpect(jsonPath("$.pageable.size").value(size))
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    @SneakyThrows
    public void shouldGetDriverFileAttributeWhenItExists() {
        final UUID driverId = UUID.randomUUID();
        final String photo = "logistics-data.company.com.br/attributes/WORKER/id-12450/WORKER_PHOTO/foto.jpg";
        final String url = "http://" + faker.internet().url();
        final String driverContent = new DriverJsonBuilder().uuid(driverId)
                .addAttribute(new DriverAttributeRequest(WORKER_PHOTO.name(), photo))
                .build();

        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(driverContent));

        when(fileRepositoryService.getUrl(eq("logistics-data.company.com.br"),
            eq("attributes/WORKER/id-12450/WORKER_PHOTO/foto.jpg"),
            anyLong(),
            anyBoolean())).thenReturn(new URL(url));

        mvc.perform(get(ROUTE_BASE_DRIVER + "/{driverUuid}/attributes/{name}/file", driverId, WORKER_PHOTO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(WORKER_PHOTO.name()))
                .andExpect(jsonPath("$.url").value(url))
                .andExpect(jsonPath("$.fileName").value("foto.jpg"));
    }

    @Test
    @SneakyThrows
    public void shouldGetNotFoundWhenDriverFileAttributeDoesNotExist() {
        final UUID driverId = UUID.randomUUID();
        final String driverContent = new DriverJsonBuilder().uuid(driverId).build();
        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(driverContent));

        mvc.perform(get(ROUTE_BASE_DRIVER + "/{driverUuid}/attributes/{name}/file", driverId, WORKER_PHOTO))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void shouldBadRequestWhenDriverAttributeNameIsNotValid() {
        final UUID driverId = UUID.randomUUID();
        final String driverContent = new DriverJsonBuilder().uuid(driverId).build();
        mvc.perform(post(ROUTE_BASE_DRIVER).contentType(MediaType.APPLICATION_JSON).content(driverContent));

        final String invalidAttributeMessage = "Driver attribute DRIVERS_LICENSE is not of file type";
        mvc.perform(get(ROUTE_BASE_DRIVER + "/{driverUuid}/attributes/{name}/file", driverId, DRIVERS_LICENSE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(invalidAttributeMessage));
    }
    
    @Test
    @SneakyThrows
    public void testFindAnonymizeDriver() {
        final Driver driver = driverFactory.createDriver();
        final String userUuid = UUID.randomUUID().toString();

        mvc.perform(delete(ROUTE_BASE_DRIVER + "/{driverUuid}", driver.getUuid())
            .contentType(MediaType.APPLICATION_JSON)
            .content(userUuid))
            .andExpect(status().isOk());

        driverAnonymizationService.anonymizeScheduled();

        mvc.perform(get(ROUTE_BASE_DRIVER + "/{driverUuid}", driver.getUuid()).contentType(MediaType.APPLICATION_JSON)
                .param("attributesSelection", "FULL_NAME, CPF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(driver.getUuid().toString()))
                .andExpect(jsonPath("$.attributes", hasSize(is(2))))
                .andExpect(jsonPath("$.attributes[*].name", hasItems(FULL_NAME.name(), CPF.name())));
    }

}
