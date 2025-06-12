package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.model.ArtistJson;
import anbrain.qa.rococo.model.page.RestPage;
import anbrain.qa.rococo.service.api.ArtistClient;
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

@Tag(name = "Artist API", description = "Операции с художниками")
@RestController
@RequestMapping("api/artist")
public class ArtistController {

    private final ArtistClient artistClient;

    @Autowired
    public ArtistController(ArtistClient artistClient) {
        this.artistClient = artistClient;
    }

    @Operation(
            summary = "Получить список художников",
            description = "Возвращает страницу с художниками. Поддерживает пагинацию"
    )
    @GetMapping
    public RestPage<ArtistJson> getAll(
            @Parameter(description = "Параметры пагинации")
            @Nonnull @Valid Pageable pageable
    ) {
        return artistClient.getAll(pageable);
    }
}
