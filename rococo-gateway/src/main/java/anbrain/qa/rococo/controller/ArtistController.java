package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.exception.RococoBadRequestException;
import anbrain.qa.rococo.model.ArtistJson;
import anbrain.qa.rococo.model.page.RestPage;
import anbrain.qa.rococo.service.grpc.ArtistGrpcClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Artist API", description = "Операции с художниками")
@RestController
@RequestMapping("api/artist")
@Validated
public class ArtistController {

    private final ArtistGrpcClient artistGrpcClient;

    @Autowired
    public ArtistController(ArtistGrpcClient artistGrpcClient) {
        this.artistGrpcClient = artistGrpcClient;
    }

    @Operation(summary = "Получить список художников с пагинацией",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страница с художниками",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RestPage.class))),
                    @ApiResponse(responseCode = "400", description = "Неверные параметры пагинации"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            })
    @GetMapping
    public ResponseEntity<RestPage<ArtistJson>> getAllArtists(
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        Page<ArtistJson> artists = artistGrpcClient.getAllArtists(pageable);
        return ResponseEntity.ok(new RestPage<>(artists.getContent(), pageable, artists.getTotalElements()));
    }

    @Operation(summary = "Получить художника по ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Найденный художник",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ArtistJson.class))),
                    @ApiResponse(responseCode = "400", description = "Неверный формат ID"),
                    @ApiResponse(responseCode = "404", description = "Художник не найден"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            })
    @GetMapping("/{id}")
    public ResponseEntity<ArtistJson> getArtistById(
            @Parameter(description = "ID художника", required = true)
            @PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            ArtistJson artist = artistGrpcClient.getArtist(uuid);
            return ResponseEntity.ok(artist);
        } catch (IllegalArgumentException e) {
            throw new RococoBadRequestException("Неверный формат ID музея: " + id);
        }
    }

    @Operation(summary = "Поиск художников по имени",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страница с найденными художниками",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RestPage.class))),
                    @ApiResponse(responseCode = "400", description = "Пустое имя для поиска или неверные параметры пагинации"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            })
    @GetMapping(params = "name")
    public ResponseEntity<RestPage<ArtistJson>> searchArtistsByName(
            @NotBlank @Parameter(description = "Имя художника для поиска", required = true)
            @RequestParam String name,
            @NotNull @Parameter(description = "Параметры пагинации") Pageable pageable) {
        Page<ArtistJson> artists = artistGrpcClient.searchArtistsByName(name, pageable);
        return ResponseEntity.ok(new RestPage<>(artists.getContent(), pageable, artists.getTotalElements()));
    }

    @Operation(summary = "Создать нового художника",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Созданный художник",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ArtistJson.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные художника"),
                    @ApiResponse(responseCode = "409", description = "Художник с таким именем уже существует"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            })
    @PostMapping
    public ResponseEntity<ArtistJson> createArtist(
            @Parameter(description = "Данные художника", required = true)
            @Valid @RequestBody ArtistJson artist) {
        ArtistJson createdArtist = artistGrpcClient.createArtist(artist);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArtist);
    }

    @Operation(summary = "Обновить данные художника",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Обновленный художник",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ArtistJson.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные художника"),
                    @ApiResponse(responseCode = "404", description = "Художник не найден"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            })
    @PutMapping
    public ResponseEntity<ArtistJson> updateArtist(
            @Parameter(description = "Обновленные данные художника", required = true)
            @Valid @RequestBody ArtistJson artist) {
        ArtistJson updatedArtist = artistGrpcClient.updateArtist(artist);
        return ResponseEntity.ok(updatedArtist);
    }
}