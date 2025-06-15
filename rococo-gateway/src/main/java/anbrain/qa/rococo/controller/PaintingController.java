package anbrain.qa.rococo.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "PaintingController API", description = "Операции с картинами")
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
                    @ApiResponse(responseCode = "404", description = "Картина не найдена")
            })
    @GetMapping("/{id}")
    public ResponseEntity<PaintingJson> getPaintingById(
            @Parameter(description = "ID картины", required = true)
            @PathVariable String id) {
        PaintingJson painting = paintingGrpcClient.getPainting(UUID.fromString(id));
        return ResponseEntity.ok(painting);
    }

    @Operation(summary = "Получить все картины с пагинацией",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страница с картинами",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RestPage.class)))
            })
    @GetMapping
    public ResponseEntity<RestPage<PaintingJson>> getAllPaintings(
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(paintingGrpcClient.getAllPaintings(pageable));
    }

    @Operation(summary = "Получить картины художника",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страница с картинами художника",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RestPage.class)))
            })
    @GetMapping("/author/{artistId}")
    public ResponseEntity<RestPage<PaintingJson>> getPaintingsByArtist(
            @Parameter(description = "ID художника", required = true)
            @PathVariable String artistId,
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(paintingGrpcClient.getPaintingsByArtist(UUID.fromString(artistId), pageable));
    }

    @Operation(summary = "Добавить новую картину",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Созданная картина",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaintingJson.class)))
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
                    @ApiResponse(responseCode = "404", description = "Картина не найдена")
            })
    @PatchMapping
    public ResponseEntity<PaintingJson> updatePainting(
            @Parameter(description = "Обновленные данные картины", required = true)
            @Valid @RequestBody PaintingJson painting) {
        PaintingJson updatedPainting = paintingGrpcClient.updatePainting(painting);
        return ResponseEntity.ok(updatedPainting);
    }
}