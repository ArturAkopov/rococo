package anbrain.qa.rococo.tests.rest;

import anbrain.qa.rococo.jupiter.annotation.ApiLogin;
import anbrain.qa.rococo.jupiter.annotation.Artist;
import anbrain.qa.rococo.jupiter.annotation.Token;
import anbrain.qa.rococo.jupiter.annotation.User;
import anbrain.qa.rococo.jupiter.annotation.meta.RestTest;
import anbrain.qa.rococo.jupiter.extension.ApiLoginExtension;
import anbrain.qa.rococo.model.rest.ArtistJson;
import anbrain.qa.rococo.model.rest.page.RestPage;
import anbrain.qa.rococo.service.rest.ArtistRestClient;
import anbrain.qa.rococo.utils.RandomDataUtils;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import lombok.NonNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
@DisplayName("[REST] Проверка работы с сервисом Artist")
public class ArtistRestTests {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

    private final ArtistRestClient artistClient = new ArtistRestClient();

    private final Pageable defaultPageable = PageRequest.of(0, 10);
    private final ArtistJson testArtist = new ArtistJson(
            null,
            RandomDataUtils.randomArtistName(),
            RandomDataUtils.randomString(),
            RandomDataUtils.avatar()
    );

    @Test
    @DisplayName("Должен успешно быть получен список художников с пагинацией")
    void shouldGetAllArtistsWithPagination() {
        RestPage artists = artistClient.getAllArtists(0, 10).as(RestPage.class);

        assertAll(
                () -> assertNotNull(artists),
                () -> assertFalse(artists.getContent().isEmpty()),
                () -> assertTrue(artists.getTotalElements() > 0)
        );
    }

    @Test
    @DisplayName("Должна вернутся корректная пагинация при запросе художников")
    void shouldReturnCorrectPagination() {
        int testSize = 5;
        RestPage artists = artistClient.getAllArtists(0, testSize).as(RestPage.class);

        assertEquals(testSize, artists.getContent().size());
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Должен успешно создаться новый художника")
    void shouldCreateArtist(@Token String testToken) {
        ArtistJson createdArtist = artistClient.createArtist(testArtist, testToken).as(ArtistJson.class);

        assertAll(
                () -> assertNotNull(createdArtist.id()),
                () -> assertEquals(testArtist.name(), createdArtist.name()),
                () -> assertEquals(testArtist.biography(), createdArtist.biography()),
                () -> assertEquals(testArtist.photo(), createdArtist.photo())
        );
    }


    @Test
    @Artist
    @DisplayName("Должен найтись созданный художник по ID")
    void shouldFindCreatedArtistById(@NonNull ArtistJson artistJson) {
        ArtistJson foundArtist = artistClient.getArtistById(artistJson.id()).as(ArtistJson.class);

        assertEquals(artistJson.id(), foundArtist.id());
    }

    @Test
    @User
    @Artist
    @ApiLogin
    @DisplayName("Должны обновиться данные художника")
    void shouldUpdateArtist(@NonNull ArtistJson artistJson, @Token String testToken) {

        String updateName = RandomDataUtils.randomArtistName();

        ArtistJson updateArtist = new ArtistJson(
                artistJson.id(),
                updateName,
                artistJson.biography(),
                artistJson.photo()
        );

        ArtistJson updatedArtist = artistClient.updateArtist(updateArtist, testToken).as(ArtistJson.class);

        assertEquals(updateName, updatedArtist.name());
    }

    @Test
    @Artist
    @DisplayName("Должны найтись художники по частичному совпадению имени")
    void shouldFindArtistsByPartialName(@NonNull ArtistJson artistJson) {
        String partialName = artistJson.name().substring(0, 3);

        RestPage<ArtistJson> foundArtists = artistClient.searchArtistsByName(
                partialName,
                defaultPageable
        ).as(new TypeRef<>() {
        });

        assertTrue(foundArtists.getContent().stream()
                .anyMatch(a -> a.name().contains(partialName)));
    }


    @Test
    @Artist
    @DisplayName("Должен вернуться пустой результат при поиске несуществующего имени")
    void shouldReturnEmptyForNonExistingName() {
        RestPage artists = artistClient.searchArtistsByName(
                "НесуществующееИмя",
                defaultPageable
        ).as(RestPage.class);

        assertTrue(artists.getContent().isEmpty());
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Должна вернуться ошибка при создании художника с пустым именем")
    void shouldFailWhenCreatingArtistWithEmptyName(@Token String testToken) {
        ArtistJson invalidArtist = new ArtistJson(
                null,
                null,
                RandomDataUtils.randomString(),
                RandomDataUtils.avatar()
        );

        Response response = artistClient.createArtist(invalidArtist, testToken);

        assertEquals(400, response.getStatusCode());
        assertEquals("400 BAD_REQUEST", response.jsonPath().getString("error.code"));
    }

    @Test
    @DisplayName("Должен вернуть ошибку при неавторизованном создании художника")
    void shouldFailWhenUnauthorized() {
        Response response = artistClient.createArtist(testArtist, "");
        assertEquals(401, response.getStatusCode());
        assertEquals("401 UNAUTHORIZED", response.jsonPath().getString("error.code"));
    }

}