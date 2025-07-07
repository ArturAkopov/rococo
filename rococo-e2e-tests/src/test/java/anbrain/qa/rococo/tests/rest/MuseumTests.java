package anbrain.qa.rococo.tests.rest;

import anbrain.qa.rococo.jupiter.annotation.ApiLogin;
import anbrain.qa.rococo.jupiter.annotation.Museum;
import anbrain.qa.rococo.jupiter.annotation.Token;
import anbrain.qa.rococo.jupiter.annotation.User;
import anbrain.qa.rococo.jupiter.annotation.meta.RestTest;
import anbrain.qa.rococo.jupiter.extension.ApiLoginExtension;
import anbrain.qa.rococo.model.rest.CountryJson;
import anbrain.qa.rococo.model.rest.GeoJson;
import anbrain.qa.rococo.model.rest.MuseumJson;
import anbrain.qa.rococo.model.rest.page.RestPage;
import anbrain.qa.rococo.service.rest.CountryRestClient;
import anbrain.qa.rococo.service.rest.MuseumRestClient;
import anbrain.qa.rococo.utils.RandomDataUtils;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
@DisplayName("Проверка работы с сервисом Museum")
public class MuseumTests {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

    private final MuseumRestClient museumClient = new MuseumRestClient();
    private final CountryRestClient countryRestClient = new CountryRestClient();
    private final Pageable pageable = PageRequest.of(0, 10);

    @Test
    @DisplayName("Должен успешно быть получен список музеев с пагинацией")
    void shouldGetAllMuseumsWithPagination() {
        RestPage<MuseumJson> museums = museumClient.getAllMuseums(
                pageable.getPageNumber(),
                pageable.getPageSize()
        ).as(new TypeRef<>() {
        });

        assertAll(
                () -> assertNotNull(museums),
                () -> assertFalse(museums.getContent().isEmpty()),
                () -> assertTrue(museums.getTotalElements() > 0)
        );
    }

    @Test
    @DisplayName("Должна вернуться корректная пагинация при запросе музеев")
    void shouldReturnCorrectPagination() {
        int testSize = 1;
        final RestPage<MuseumJson> museums = museumClient.getAllMuseums(
                pageable.getPageNumber(),
                testSize
        ).as(new TypeRef<>() {
        });

        assertEquals(testSize, museums.getContent().size());
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Должен успешно создаться новый музей")
    void shouldCreateMuseum(@Token String testToken) {

        final RestPage<CountryJson> page = countryRestClient.getAllCountries(pageable.getPageNumber(), pageable.getPageSize(), testToken)
                .as(new TypeRef<>() {
                });
        final CountryJson countryJson = page.getContent().getFirst();
        final MuseumJson newMuseum = getRandomMuseum(countryJson, null);

        final MuseumJson createdMuseum = museumClient.createMuseum(newMuseum, testToken)
                .as(MuseumJson.class);

        assertAll(
                () -> assertNotNull(createdMuseum.id()),
                () -> assertEquals(newMuseum.title(), createdMuseum.title()),
                () -> assertEquals(newMuseum.description(), createdMuseum.description()),
                () -> assertEquals(newMuseum.geo().city(), createdMuseum.geo().city()),
                () -> assertEquals(newMuseum.geo().country().name(), createdMuseum.geo().country().name()),
                () -> assertEquals(newMuseum.photo(), createdMuseum.photo())
        );
    }

    @Test
    @Museum
    @DisplayName("Должен найтись созданный музей по ID")
    void shouldFindCreatedMuseumById(@NonNull MuseumJson museumJson) {
        MuseumJson foundMuseum = museumClient.getMuseumById(
                museumJson.id().toString()
        ).as(MuseumJson.class);

        assertEquals(museumJson.id(), foundMuseum.id());
    }

    @Test
    @User
    @Museum
    @ApiLogin
    @DisplayName("Должны обновиться данные музея")
    void shouldUpdateMuseum(@NonNull MuseumJson museumJson, @Token String testToken) {
        final RestPage<CountryJson> page = countryRestClient.getAllCountries(pageable.getPageNumber(), pageable.getPageSize(), testToken)
                .as(new TypeRef<>() {
                });
        final CountryJson countryJson = page.getContent().getFirst();

        MuseumJson updateMuseum = getRandomMuseum(countryJson, museumJson.id());

        MuseumJson updatedMuseum = museumClient.updateMuseum(updateMuseum, testToken)
                .as(MuseumJson.class);

        assertAll(
                () -> assertEquals(updateMuseum.title(), updatedMuseum.title()),
                () -> assertEquals(updateMuseum.description(), updatedMuseum.description()),
                () -> assertEquals(updateMuseum.geo().city(), updatedMuseum.geo().city()),
                () -> assertEquals(updateMuseum.geo().country().name(), updatedMuseum.geo().country().name()),
                () -> assertEquals(updateMuseum.photo(), updatedMuseum.photo())
        );
    }

    @Test
    @Museum
    @DisplayName("Должны найтись музеи по частичному совпадению названия")
    void shouldFindMuseumsByPartialTitle(@NonNull MuseumJson museumJson) {
        String partialTitle = museumJson.title().substring(0, 3);

        RestPage<MuseumJson> foundMuseums = museumClient.searchMuseumsByTitle(
                partialTitle,
                pageable.getPageNumber(),
                pageable.getPageSize()
        ).as(new TypeRef<>() {
        });

        assertTrue(foundMuseums.getContent().stream()
                .anyMatch(m -> m.title().contains(partialTitle)));
    }

    @Test
    @Museum
    @DisplayName("Должен вернуться пустой результат при поиске несуществующего названия")
    void shouldReturnEmptyForNonExistingTitle() {
        RestPage museums = museumClient.searchMuseumsByTitle(
                "НесуществующееНазвание",
                pageable.getPageNumber(),
                pageable.getPageSize()
        ).as(RestPage.class);

        assertTrue(museums.getContent().isEmpty());
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Должна вернуться ошибка при создании музея с пустым названием")
    void shouldFailWhenCreatingMuseumWithEmptyTitle(@Token String testToken) {

        final RestPage<CountryJson> page = countryRestClient.getAllCountries(pageable.getPageNumber(), pageable.getPageSize(), testToken)
                .as(new TypeRef<>() {
                });
        final CountryJson countryJson = page.getContent().getFirst();

        MuseumJson invalidMuseum = new MuseumJson(
                null,
                null,
                RandomDataUtils.randomString(),
                RandomDataUtils.avatar(),
                new GeoJson(
                        RandomDataUtils.randomCity(),
                        new CountryJson(
                                countryJson.id(),
                                countryJson.name()
                        )
                )
        );

        Response response = museumClient.createMuseum(invalidMuseum, testToken);

        assertEquals(400, response.getStatusCode());
        assertEquals("400 BAD_REQUEST", response.jsonPath().getString("error.code"));
    }

    @Test
    @Museum
    @DisplayName("Должна вернуться ошибка при неавторизованном создании музея")
    void shouldFailWhenUnauthorized(MuseumJson museumJson) {
        Response response = museumClient.createMuseum(museumJson, "");

        assertEquals(401, response.getStatusCode());
        assertEquals("401 UNAUTHORIZED", response.jsonPath().getString("error.code"));
    }

    @Test
    @DisplayName("Должна вернуться ошибка при запросе невалидного ID музея")
    void shouldFailWhenInvalidMuseumId() {
        Response response = museumClient.getMuseumById("invalid-id");

        assertEquals(400, response.getStatusCode());
        assertEquals("400 BAD_REQUEST", response.jsonPath().getString("error.code"));
    }

    @NonNull
    private MuseumJson getRandomMuseum(@NonNull CountryJson countryJson, @Nullable UUID id) {
        return new MuseumJson(
                id,
                RandomDataUtils.randomMuseumTitle(),
                RandomDataUtils.randomString(),
                RandomDataUtils.avatar(),
                new GeoJson(
                        RandomDataUtils.randomCity(),
                        new CountryJson(
                                countryJson.id(),
                                countryJson.name()
                        )
                )
        );
    }
}