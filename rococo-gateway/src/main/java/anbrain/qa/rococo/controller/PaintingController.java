package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.exception.RococoBadRequestException;
import anbrain.qa.rococo.model.PaintingJson;
import anbrain.qa.rococo.model.page.RestPage;
import anbrain.qa.rococo.service.grpc.PaintingGrpcClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Painting API", description = "Операции с картинами")
@RestController
@RequestMapping("api/painting")
public class PaintingController {

    private final PaintingGrpcClient paintingGrpcClient;

    @Autowired
    public PaintingController(PaintingGrpcClient paintingGrpcClient) {
        this.paintingGrpcClient = paintingGrpcClient;
    }

    @Operation(summary = "Получить картину по ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Найденная картина",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaintingJson.class))),
                    @ApiResponse(responseCode = "400", description = "Неверный формат ID"),
                    @ApiResponse(responseCode = "404", description = "Картина не найдена"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            })
    @GetMapping("/{id}")
    public ResponseEntity<PaintingJson> getPaintingById(
            @Parameter(description = "ID картины", required = true)
            @PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            PaintingJson painting = paintingGrpcClient.getPainting(uuid);
            return ResponseEntity.ok(painting);
        } catch (IllegalArgumentException e) {
            throw new RococoBadRequestException("Неверный формат ID картины: " + id);
        }
    }

    @Operation(summary = "Получить все картины с пагинацией",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страница с картинами",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RestPage.class))),
                    @ApiResponse(responseCode = "400", description = "Неверные параметры пагинации"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            })
    @GetMapping
    public ResponseEntity<RestPage<PaintingJson>> getAllPaintings(
            @NotNull @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(paintingGrpcClient.getAllPaintings(pageable));
    }

    @Operation(summary = "Получить картины художника",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страница с картинами художника",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RestPage.class))),
                    @ApiResponse(responseCode = "400", description = "Неверный формат ID художника"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            })
    @GetMapping("/author/{artistId}")
    public ResponseEntity<RestPage<PaintingJson>> getPaintingsByArtist(
            @Parameter(description = "ID художника", required = true)
            @PathVariable String artistId,
            @NotNull @Parameter(description = "Параметры пагинации") Pageable pageable) {
        try {
            UUID uuid = UUID.fromString(artistId);
            return ResponseEntity.ok(paintingGrpcClient.getPaintingsByArtist(uuid, pageable));
        } catch (IllegalArgumentException e) {
            throw new RococoBadRequestException("Неверный формат ID художника: " + artistId);
        }
    }

    @Operation(summary = "Поиск картин по названию",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страница с найденными картинами",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RestPage.class))),
                    @ApiResponse(responseCode = "400", description = "Неверные параметры пагинации"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            })
    @GetMapping(params = "title")
    public ResponseEntity<RestPage<PaintingJson>> getPaintingsByTitle(
            @Parameter(description = "Название картины для поиска", required = true)
            @RequestParam String title,
            @NotNull @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(paintingGrpcClient.getPaintingsByTitle(title, pageable));
    }

    @Operation(summary = "Добавить новую картину",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Созданная картина",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaintingJson.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные картины"),
                    @ApiResponse(responseCode = "409", description = "Картина с таким названием уже существует"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            })
    @PostMapping
    public ResponseEntity<PaintingJson> createPainting(
            @Parameter(description = "Данные картины", required = true)
            @Valid @RequestBody PaintingJson painting) {
        PaintingJson createdPainting = paintingGrpcClient.createPainting(painting);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPainting);
    }

    @Operation(summary = "Обновить данные картины",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Обновленная картина",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaintingJson.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные картины"),
                    @ApiResponse(responseCode = "404", description = "Картина не найдена"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            })
    @PutMapping
    public ResponseEntity<PaintingJson> updatePainting(
            @Parameter(description = "Обновленные данные картины", required = true)
            @Valid @RequestBody PaintingJson painting) {
        PaintingJson updatedPainting = paintingGrpcClient.updatePainting(painting);
        return ResponseEntity.ok(updatedPainting);
    }
}