package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.model.MuseumJson;
import anbrain.qa.rococo.model.page.RestPage;
import anbrain.qa.rococo.service.api.MuseumClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MuseumController API", description = "Операции с музеями")
@RestController
@RequestMapping("api/museum")
public class MuseumController {

    private final MuseumClient museumClient;

    @Autowired
    public MuseumController(MuseumClient museumClient) {
        this.museumClient = museumClient;
    }

    @Operation(
            summary = "Получить список музеев",
            description = "Возвращает страницу с музеями. Поддерживает пагинацию"
    )
    @GetMapping
    public RestPage<MuseumJson> getAll(
            @Parameter(description = "Параметры пагинации")
            @Nonnull @Valid Pageable pageable
    ) {
        return museumClient.getAll(pageable);
    }
}
