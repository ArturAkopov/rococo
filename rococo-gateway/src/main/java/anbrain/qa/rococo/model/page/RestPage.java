package anbrain.qa.rococo.model.page;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Schema(
        description = "Страница с результатами",
        example = """
                {
                  "content": [
                    {
                      "id": "550e8400-e29b-41d4-a716-446655440000",
                      "title": "Звёздная ночь",
                      "artist": {
                        "name": "Ван Гог"
                      }
                    }
                  ],
                  "pageable": {
                    "pageNumber": 0,
                    "pageSize": 10,
                    "sort": {
                      "empty": true,
                      "sorted": false,
                      "unsorted": true
                    },
                    "offset": 0,
                    "paged": true,
                    "unpaged": false
                  },
                  "last": false,
                  "totalPages": 5,
                  "totalElements": 50,
                  "number": 0,
                  "size": 10,
                  "sort": {
                    "empty": true,
                    "sorted": false,
                    "unsorted": true
                  },
                  "first": true,
                  "numberOfElements": 10,
                  "empty": false
                }"""
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestPage<T> extends PageImpl<T> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestPage(
            @Schema(description = "Список элементов на текущей странице", requiredMode = Schema.RequiredMode.REQUIRED)
            @JsonProperty("content")
            List<T> content,

            @Schema(description = "Параметры пагинации", requiredMode = Schema.RequiredMode.REQUIRED, implementation = Pageable.class)
            @JsonProperty("pageable")
            JsonNode pageable,

            @Schema(description = "Является ли текущая страница последней", example = "false")
            @JsonProperty("last")
            boolean last,

            @Schema(description = "Общее количество страниц", example = "5")
            @JsonProperty("totalPages")
            int totalPages,

            @Schema(description = "Общее количество элементов", example = "50")
            @JsonProperty("totalElements")
            Long totalElements,

            @Schema(description = "Номер текущей страницы (0-базовая)", example = "0")
            @JsonProperty("number")
            int number,

            @Schema(description = "Является ли текущая страница первой", example = "true")
            @JsonProperty("first")
            boolean first,

            @Schema(description = "Параметры сортировки", example = """
                    {
                      "empty": true,
                      "sorted": false,
                      "unsorted": true
                    }""")
            @JsonProperty("sort")
            JsonNode sort,

            @Schema(description = "Количество элементов на странице", example = "10")
            @JsonProperty("size")
            int size,

            @Schema(description = "Количество элементов на текущей странице", example = "10")
            @JsonProperty("numberOfElements")
            int numberOfElements) {

        super(content, PageRequest.of(number, size), totalElements);
    }

    public RestPage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public RestPage(List<T> content) {
        super(content);
    }

    public RestPage() {
        super(new ArrayList<>());
    }
}
