package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.exception.RococoBadRequestException;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@Tag(name = "Museum API", description = "Операции с музеями")
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
                    @ApiResponse(responseCode = "400", description = "Неверный формат ID"),
                    @ApiResponse(responseCode = "404", description = "Музей не найден"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            })
    @GetMapping("/{id}")
    public ResponseEntity<MuseumJson> getMuseumById(
            @Parameter(description = "ID музея", required = true)
            @PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            MuseumJson museum = museumGrpcClient.getMuseum(uuid);
            return ResponseEntity.ok(museum);
        } catch (IllegalArgumentException e) {
            throw new RococoBadRequestException("Неверный формат ID музея: " + id);
        }
    }

    @Operation(summary = "Получить все музеи с пагинацией",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страница с музеями",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RestPage.class))),
                    @ApiResponse(responseCode = "400", description = "Неверные параметры пагинации"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            })
    @GetMapping
    public ResponseEntity<RestPage<MuseumJson>> getAllMuseums(
            @NotNull @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(museumGrpcClient.getAllMuseums(pageable));
    }

    @Operation(summary = "Поиск музеев по названию",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список найденных музеев",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MuseumJson.class, type = "array"))),
                    @ApiResponse(responseCode = "400", description = "Пустое название для поиска"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            })
    @GetMapping(params = "title")
    public ResponseEntity<RestPage<MuseumJson>> searchMuseumsByTitle(
            @NotBlank @Parameter(description = "Название музея для поиска", required = true)
            @RequestParam String title,
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(new RestPage<>(museumGrpcClient.searchMuseumsByTitle(title)));
    }

    @Operation(summary = "Добавить новый музей",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Созданный музей",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MuseumJson.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные музея"),
                    @ApiResponse(responseCode = "409", description = "Музей с таким названием уже существует"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
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
                    @ApiResponse(responseCode = "400", description = "Невалидные данные музея"),
                    @ApiResponse(responseCode = "404", description = "Музей не найден"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            })
    @PutMapping
    public ResponseEntity<MuseumJson> updateMuseum(
            @Parameter(description = "Обновленные данные музея", required = true)
            @Valid @RequestBody MuseumJson museum) {
        MuseumJson updatedMuseum = museumGrpcClient.updateMuseum(museum);
        return ResponseEntity.ok(updatedMuseum);
    }
}