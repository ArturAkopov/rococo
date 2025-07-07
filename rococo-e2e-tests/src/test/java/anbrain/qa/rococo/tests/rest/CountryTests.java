package anbrain.qa.rococo.tests.rest;

import anbrain.qa.rococo.jupiter.annotation.ApiLogin;
import anbrain.qa.rococo.jupiter.annotation.Token;
import anbrain.qa.rococo.jupiter.annotation.User;
import anbrain.qa.rococo.jupiter.annotation.meta.RestTest;
import anbrain.qa.rococo.jupiter.extension.ApiLoginExtension;
import anbrain.qa.rococo.model.rest.CountryJson;
import anbrain.qa.rococo.model.rest.page.RestPage;
import anbrain.qa.rococo.service.rest.CountryRestClient;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
@DisplayName("Проверка работы с сервисом Country")
public class CountryTests {

    @RegisterExtension
    ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

    private final CountryRestClient countryClient = new CountryRestClient();
    private final Pageable pageable = PageRequest.of(0, 10);

    @Test
    @User
    @ApiLogin
    @DisplayName("Должен быть успешно получен список стран с пагинацией")
    void shouldGetAllCountriesWithPagination(@Token String token) {
        RestPage<CountryJson> countries = countryClient.getAllCountries(pageable.getPageNumber(),pageable.getPageSize(), token)
                .as(new TypeRef<>() {
                });

        assertAll(
                () -> assertNotNull(countries),
                () -> assertFalse(countries.getContent().isEmpty()),
                () -> assertTrue(countries.getTotalElements() > 0)
        );
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Должна вернуться корректная пагинация при запросе стран")
    void shouldReturnCorrectPagination(@Token String token) {
        int testSize = 5;
        RestPage<CountryJson> countries = countryClient.getAllCountries(pageable.getPageNumber(), testSize, token)
                .as(new TypeRef<>() {
                });

        assertEquals(testSize, countries.getContent().size());
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Должны содержаться ожидаемые поля у страны")
    void shouldContainRequiredFields(@Token String token) {
        RestPage<CountryJson> countries = countryClient.getAllCountries(pageable.getPageNumber(),pageable.getPageSize(), token)
                .as(new TypeRef<>() {
                });

        CountryJson firstCountry = countries.getContent().getFirst();

        assertAll(
                () -> assertNotNull(firstCountry.id()),
                () -> assertNotNull(firstCountry.name())
        );
    }

    @Test
    @DisplayName("Должна вернуться ошибка при неавторизованном запросе списка стран")
    void shouldFailWhenUnauthorized() {
        Response response = countryClient.getAllCountries(pageable.getPageNumber(),pageable.getPageSize(), "");

        assertAll(
                () -> assertEquals(401, response.getStatusCode()),
                () -> assertEquals("401 UNAUTHORIZED", response.jsonPath().getString("error.code"))
        );
    }
}