package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.model.MuseumJson;
import anbrain.qa.rococo.model.page.RestPage;
import anbrain.qa.rococo.service.grpc.MuseumGrpcClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "MuseumController API", description = "Операции с музеями")
@RestController
@RequestMapping("api/museum")
public class MuseumController {

    private final MuseumGrpcClient museumGrpcClient;

    @Autowired
    public MuseumController(MuseumGrpcClient museumGrpcClient) {
        this.museumGrpcClient = museumGrpcClient;
    }

    @Operation(summary = "Получить музей по ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Найденный музей",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MuseumJson.class))),
                    @ApiResponse(responseCode = "404", description = "Музей не найден")
            })
    @GetMapping("/{id}")
    public ResponseEntity<MuseumJson> getMuseumById(
            @Parameter(description = "ID музея", required = true)
            @PathVariable String id) {
        MuseumJson museum = museumGrpcClient.getMuseum(UUID.fromString(id));
        return ResponseEntity.ok(museum);
    }

    @Operation(summary = "Получить все музеи с пагинацией",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страница с музеями",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RestPage.class)))
            })
    @GetMapping
    public ResponseEntity<RestPage<MuseumJson>> getAllMuseums(
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(museumGrpcClient.getAllMuseums(pageable));
    }

    @Operation(summary = "Поиск музеев по названию",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страница с найденными музеями",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RestPage.class)))
            })
    @GetMapping(params = "title")
    public ResponseEntity<RestPage<MuseumJson>> searchMuseumsByTitle(
            @Parameter(description = "Название музея для поиска", required = true)
            @RequestParam String title,
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(new RestPage<>(museumGrpcClient.searchMuseumsByTitle(title)));
    }

    @Operation(summary = "Добавить новый музей",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Созданный музей",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MuseumJson.class)))
            })
    @PostMapping
    public ResponseEntity<MuseumJson> createMuseum(
            @Parameter(description = "Данные музея", required = true)
            @Valid @RequestBody MuseumJson museum) {
        MuseumJson createdMuseum = museumGrpcClient.createMuseum(museum);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMuseum);
    }

    @Operation(summary = "Обновить данные музея",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Обновленный музей",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MuseumJson.class))),
                    @ApiResponse(responseCode = "404", description = "Музей не найден")
            })
    @PatchMapping
    public ResponseEntity<MuseumJson> updateMuseum(
            @Parameter(description = "Обновленные данные музея", required = true)
            @Valid @RequestBody MuseumJson museum) {
        MuseumJson updatedMuseum = museumGrpcClient.updateMuseum(museum);
        return ResponseEntity.ok(updatedMuseum);
    }
}
