package anbrain.qa.rococo.model.rest.page;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestPage<T> extends PageImpl<T> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestPage(
            @JsonProperty("content")
            List<T> content,

            @JsonProperty("pageable")
            JsonNode pageable,

            @JsonProperty("last")
            boolean last,

            @JsonProperty("totalPages")
            int totalPages,

            @JsonProperty("totalElements")
            Long totalElements,

            @JsonProperty("number")
            int number,

            @JsonProperty("first")
            boolean first,

            @JsonProperty("sort")
            JsonNode sort,

            @JsonProperty("size")
            int size,

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
