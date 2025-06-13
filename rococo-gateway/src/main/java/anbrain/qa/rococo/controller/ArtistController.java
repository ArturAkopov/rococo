package anbrain.qa.rococo.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Artist API", description = "Операции с художниками")
@RestController
@RequestMapping("api/artist")
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
                                    schema = @Schema(implementation = RestPage.class)))
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
                    @ApiResponse(responseCode = "404", description = "Художник не найден")
            })
    @GetMapping("/{id}")
    public ResponseEntity<ArtistJson> getArtistById(
            @Parameter(description = "ID художника", required = true)
            @PathVariable UUID id) {
        ArtistJson artist = artistGrpcClient.getArtist(id);
        return ResponseEntity.ok(artist);
    }

    @Operation(summary = "Поиск художников по имени",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страница с найденными художниками",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RestPage.class)))
            })
    @GetMapping(params = "name")
    public ResponseEntity<RestPage<ArtistJson>> searchArtistsByName(
            @Parameter(description = "Имя художника для поиска", required = true)
            @RequestParam String name,
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        Page<ArtistJson> artists = artistGrpcClient.searchArtistsByName(name, pageable);
        return ResponseEntity.ok(new RestPage<>(artists.getContent(), pageable, artists.getTotalElements()));
    }

    @Operation(summary = "Создать нового художника",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Созданный художник",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ArtistJson.class)))
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
                    @ApiResponse(responseCode = "404", description = "Художник не найден")
            })
    @PutMapping
    public ResponseEntity<ArtistJson> updateArtist(
            @Parameter(description = "Обновленные данные художника", required = true)
            @Valid @RequestBody ArtistJson artist) {
        ArtistJson updatedArtist = artistGrpcClient.updateArtist(artist);
        return ResponseEntity.ok(updatedArtist);
    }
}