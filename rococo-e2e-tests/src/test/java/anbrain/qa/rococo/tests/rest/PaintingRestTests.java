package anbrain.qa.rococo.tests.rest;

import anbrain.qa.rococo.jupiter.annotation.*;
import anbrain.qa.rococo.jupiter.annotation.meta.RestTest;
import anbrain.qa.rococo.jupiter.extension.ApiLoginExtension;
import anbrain.qa.rococo.model.rest.ArtistJson;
import anbrain.qa.rococo.model.rest.MuseumJson;
import anbrain.qa.rococo.model.rest.PaintingFullJson;
import anbrain.qa.rococo.model.rest.PaintingJson;
import anbrain.qa.rococo.model.rest.page.RestPage;
import anbrain.qa.rococo.service.rest.PaintingRestClient;
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
@DisplayName("Проверка работы с сервисом Painting")
public class PaintingRestTests {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

    private final PaintingRestClient paintingClient = new PaintingRestClient();
    private final Pageable pageable = PageRequest.of(0, 10);

    @Test
    @Painting
    @Museum
    @Artist
    @DisplayName("Должен успешно быть получен список картин с пагинацией")
    void shouldGetAllPaintingsWithPagination() {
        final RestPage<PaintingFullJson> paintings = paintingClient.getAllPaintings(
                pageable.getPageNumber(),
                pageable.getPageSize()
        ).as(new TypeRef<>() {
        });

        assertAll(
                () -> assertNotNull(paintings),
                () -> assertFalse(paintings.getContent().isEmpty()),
                () -> assertTrue(paintings.getTotalElements() > 0)
        );
    }

    @Test
    @Museum
    @Artist
    @Painting
    @DisplayName("Должна вернуться корректная пагинация при запросе картин")
    void shouldReturnCorrectPagination() {
        int testSize = 1;
        final RestPage<PaintingFullJson> paintings = paintingClient.getAllPaintings(
                pageable.getPageNumber(),
                testSize
        ).as(new TypeRef<>() {
        });

        assertEquals(testSize, paintings.getContent().size());
    }

    @Test
    @User
    @Museum
    @Artist
    @ApiLogin
    @DisplayName("Должна успешно создаться новая картина")
    void shouldCreatePainting(@Token String testToken, @NonNull MuseumJson museumJson, @NonNull ArtistJson artistJson) {
        final PaintingFullJson newPainting = new PaintingFullJson(
                null,
                RandomDataUtils.randomPaintingTitle(),
                RandomDataUtils.randomString(),
                RandomDataUtils.avatar(),
                museumJson,
                artistJson
        );

        final PaintingFullJson createdPainting = paintingClient.createPainting(newPainting, testToken)
                .as(PaintingFullJson.class);

        assertAll(
                () -> assertNotNull(createdPainting.id()),
                () -> assertEquals(newPainting.title(), createdPainting.title()),
                () -> assertEquals(newPainting.description(), createdPainting.description()),
                () -> assertEquals(newPainting.content(), createdPainting.content())
        );
    }

    @Test
    @Museum
    @Artist
    @Painting
    @DisplayName("Должна найтись созданная картина по ID")
    void shouldFindCreatedPaintingById(@NonNull PaintingJson paintingJson) {
        final PaintingFullJson foundPainting = paintingClient.getPaintingById(
                paintingJson.id().toString()
        ).as(PaintingFullJson.class);

        assertEquals(paintingJson.id(), foundPainting.id());
    }

    @Test
    @User
    @Museum
    @Artist
    @Painting
    @ApiLogin
    @DisplayName("Должны обновиться данные картины")
    void shouldUpdatePainting(@NonNull PaintingJson paintingJson, @NonNull MuseumJson museumJson, @NonNull ArtistJson artistJson, @Token String testToken) {
        final PaintingFullJson updatePainting = new PaintingFullJson(
                paintingJson.id(),
                paintingJson.title(),
                paintingJson.description(),
                paintingJson.content(),
                museumJson,
                artistJson
        );

        final PaintingFullJson updatedPainting = paintingClient.updatePainting(updatePainting, testToken)
                .as(PaintingFullJson.class);

        assertAll(
                () -> assertEquals(updatePainting.title(), updatedPainting.title()),
                () -> assertEquals(updatePainting.description(), updatedPainting.description()),
                () -> assertEquals(updatePainting.content(), updatedPainting.content())
        );
    }

    @Test
    @Museum
    @Artist
    @Painting
    @DisplayName("Должны найтись картины по частичному совпадению названия")
    void shouldFindPaintingsByPartialTitle(@NonNull PaintingJson paintingJson) {
        final String partialTitle = paintingJson.title().substring(0, 3);

        final RestPage<PaintingFullJson> foundPaintings = paintingClient.getPaintingsByTitle(
                partialTitle,
                pageable.getPageNumber(),
                pageable.getPageSize()
        ).as(new TypeRef<>() {
        });

        assertTrue(foundPaintings.getContent().stream()
                .anyMatch(p -> p.title().contains(partialTitle)));
    }

    @Test
    @Museum
    @Artist
    @Painting
    @DisplayName("Должны найтись картины по ID художника")
    void shouldFindPaintingsByArtistId(@NonNull PaintingJson paintingJson) {

        RestPage<PaintingFullJson> foundPaintings = paintingClient.getPaintingsByArtist(
                paintingJson.artistId(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        ).as(new TypeRef<>() {
        });

        assertAll(
                () -> assertFalse(foundPaintings.getContent().isEmpty()),
                () -> assertTrue(foundPaintings.getContent().stream()
                        .allMatch(p -> p.artist().id().equals(UUID.fromString(paintingJson.artistId())))
                ));
    }

    @Test
    @DisplayName("Должна вернуться пустая страница при запросе картин несуществующего художника")
    void shouldReturnEmptyForNonExistingArtist() {
        String nonExistingArtistId = UUID.randomUUID().toString();

        RestPage foundPaintings = paintingClient.getPaintingsByArtist(
                nonExistingArtistId,
                pageable.getPageNumber(),
                pageable.getPageSize()
        ).as(new TypeRef<>() {
        });

        assertTrue(foundPaintings.getContent().isEmpty());
    }

    @Test
    @DisplayName("Должна вернуться ошибка при запросе картин с невалидным ID художника")
    void shouldFailWhenInvalidArtistId() {
        Response response = paintingClient.getPaintingsByArtist(
                "invalid-artist-id",
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        assertEquals(400, response.getStatusCode());
        assertEquals("400 BAD_REQUEST", response.jsonPath().getString("error.code"));
    }

    @Test
    @Painting
    @Museum
    @Artist
    @DisplayName("Должен вернуться пустой результат при поиске несуществующего названия")
    void shouldReturnEmptyForNonExistingTitle() {
        final RestPage paintings = paintingClient.getPaintingsByTitle(
                "НесуществующееНазвание",
                pageable.getPageNumber(),
                pageable.getPageSize()
        ).as(RestPage.class);

        assertTrue(paintings.getContent().isEmpty());
    }

    @Test
    @User
    @Museum
    @Artist
    @ApiLogin
    @DisplayName("Должна вернуться ошибка при создании картины с пустым названием")
    void shouldFailWhenCreatingPaintingWithEmptyTitle(@Token String testToken, @NonNull MuseumJson museumJson, @NonNull ArtistJson artistJson) {
        PaintingFullJson invalidPainting = new PaintingFullJson(
                null,
                null,
                RandomDataUtils.randomString(),
                RandomDataUtils.avatar(),
                museumJson,
                artistJson
        );

        Response response = paintingClient.createPainting(invalidPainting, testToken);

        assertEquals(400, response.getStatusCode());
        assertEquals("400 BAD_REQUEST", response.jsonPath().getString("error.code"));
    }

    @Test
    @Museum
    @Artist
    @DisplayName("Должна вернуться ошибка при неавторизованном создании картины")
    void shouldFailWhenUnauthorized(@NonNull MuseumJson museumJson, @NonNull ArtistJson artistJson) {
        Response response = paintingClient.createPainting(new PaintingFullJson(
                null,
                RandomDataUtils.randomPaintingTitle(),
                RandomDataUtils.randomString(),
                RandomDataUtils.avatar(),
                museumJson,
                artistJson
        ), "");

        assertEquals(401, response.getStatusCode());
        assertEquals("401 UNAUTHORIZED", response.jsonPath().getString("error.code"));
    }

    @Test
    @DisplayName("Должна вернуться ошибка при запросе невалидного ID картины")
    void shouldFailWhenInvalidPaintingId() {
        Response response = paintingClient.getPaintingById("invalid-id");

        assertEquals(400, response.getStatusCode());
        assertEquals("400 BAD_REQUEST", response.jsonPath().getString("error.code"));
    }

    @NonNull
    private PaintingJson getRandomPainting(@Nullable UUID id, UUID museumId, UUID artistId) {
        return new PaintingJson(
                id,
                RandomDataUtils.randomPaintingTitle(),
                RandomDataUtils.randomString(),
                RandomDataUtils.avatar(),
                String.valueOf(museumId),
                String.valueOf(artistId)
        );
    }
}