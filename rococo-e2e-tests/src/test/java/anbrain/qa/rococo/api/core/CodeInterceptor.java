package anbrain.qa.rococo.api.core;

import anbrain.qa.rococo.jupiter.extension.ApiLoginExtension;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

public class CodeInterceptor implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           @NonNull FilterContext ctx) {
        Response response = ctx.next(requestSpec, responseSpec);

        if (response.getStatusCode() >= 200 && response.getStatusCode() < 400) {
            String location = response.getHeader("Location");
            if (location != null && location.contains("code=")) {
                ApiLoginExtension.setCode(
                        StringUtils.substringAfter(location, "code=")
                );
            }
        }

        return response;
    }
}