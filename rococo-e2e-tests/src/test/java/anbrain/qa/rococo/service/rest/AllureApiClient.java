package anbrain.qa.rococo.service.rest;

import anbrain.qa.rococo.config.Config;
import anbrain.qa.rococo.model.allure.AllureResults;
import anbrain.qa.rococo.model.allure.Project;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class AllureApiClient {

    private static final Config CFG = Config.getInstance();
    private final RequestSpecification requestSpec;

    public AllureApiClient() {
        this.requestSpec = given()
                .baseUri(CFG.allureDockerServiceUrl())
                .contentType("application/json")
                .accept("application/json");
    }

    @Step("Создание allure проекта {projectId}")
    public void createProject(String projectId) {
        Response response = given(requestSpec)
                .body(new Project(projectId))
                .when()
                .post("/allure-docker-service/projects")
                .then()
                .log().ifError()
                .extract().response();

        if (response.statusCode() != 201) {
            throw new AssertionError("Failed to create project. Status: " + response.statusCode() +
                                     ", Body: " + response.getBody().asString());
        }
    }

    @Step("Отправка allure-results для проекта {projectId}")
    public void sendResults(String projectId, AllureResults allureResults) {
        Response response = given(requestSpec)
                .body(allureResults)
                .when()
                .post("/allure-docker-service/send-results?project_id=" + projectId)
                .then()
                .log().ifError()
                .extract().response();

        if (response.statusCode() != 200) {
            throw new AssertionError("Failed to send results. Status: " + response.statusCode() +
                                     ", Body: " + response.getBody().asString());
        }
    }

    @Step("Генерация allure отчета для проекта {projectId}")
    public void generateReport(String projectId, String executionName,
                               String executionFrom, String executionType) {
        Response response = given(requestSpec)
                .queryParam("project_id", projectId)
                .queryParam("execution_name", executionName)
                .queryParam("execution_from", executionFrom)
                .queryParam("execution_type", executionType)
                .when()
                .get("/allure-docker-service/generate-report")
                .then()
                .log().ifError()
                .extract().response();

        if (response.statusCode() != 200) {
            throw new AssertionError("Failed to generate report. Status: " + response.statusCode() +
                                     ", Body: " + response.getBody().asString());
        }
    }

}
