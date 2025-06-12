package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.model.PaintingJson;
import anbrain.qa.rococo.model.page.RestPage;
import anbrain.qa.rococo.service.api.PaintingClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Painting API", description = "Операции с картинами")
@RestController
@RequestMapping("api/painting")
public class PaintingController {

    private final PaintingClient paintingClient;

    @Autowired
    public PaintingController(PaintingClient paintingClient) {
        this.paintingClient = paintingClient;
    }

    @Operation(
            summary = "Получить список картин",
            description = "Возвращает страницу с картинами. Поддерживает пагинацию и фильтрацию по имени."
    )
    @GetMapping
    public RestPage<PaintingJson> getAll(
            @Parameter(description = "Фильтр по названию картины (макс. 20 символов)", example = "Звездная ночь")
            @RequestParam(required = false) @Size(max = 20, message = "Имя не должно превышать 20 символов") String name,
            @Parameter(description = "Параметры пагинации")
            @Nonnull @Valid Pageable pageable
    ) {
        return paintingClient.getAll(name, pageable);
    }
}
