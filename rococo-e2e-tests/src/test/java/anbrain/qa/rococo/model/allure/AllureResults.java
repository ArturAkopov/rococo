package anbrain.qa.rococo.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AllureResults(
        @JsonProperty("results")
        List<AllureResult> results
) {
}