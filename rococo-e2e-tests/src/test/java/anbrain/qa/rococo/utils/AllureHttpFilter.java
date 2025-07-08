package anbrain.qa.rococo.utils;

import io.qameta.allure.restassured.AllureRestAssured;

public class AllureHttpFilter {
    private static final AllureRestAssured FILTER = new AllureRestAssured();

    public static AllureRestAssured withCustomTemplates() {
        FILTER.setRequestTemplate("http-request.ftl");
        FILTER.setResponseTemplate("http-response.ftl");
        return FILTER;
    }
}