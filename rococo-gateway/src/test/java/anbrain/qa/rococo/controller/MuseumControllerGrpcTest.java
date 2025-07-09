package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.grpc.AllMuseumsResponse;
import anbrain.qa.rococo.grpc.MuseumResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.wiremock.grpc.dsl.WireMockGrpc;

import static anbrain.qa.rococo.utils.ContractTestGrpcUtils.loadProtoResponse;
import static anbrain.qa.rococo.utils.ContractTestGrpcUtils.loadRequestJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wiremock.grpc.dsl.WireMockGrpc.Status.*;


@Tag("ContractTest")
class MuseumControllerGrpcTest extends BaseControllerTest {

    private final String museumId = "3b785453-0d5b-4328-8380-5f226cb4dd5a";

    @Test
    void getMuseumById_ShouldReturnMuseumFromGrpc() throws Exception {
        final MuseumResponse response = loadProtoResponse(
                WIREMOCK_ROOT + MUSEUM_RESPONSE_PATH + "museum_response.json",
                MuseumResponse::newBuilder
        );

        mockMuseumService.stubFor(
                WireMockGrpc.method("GetMuseum")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(get("/api/museum/{id}", museumId)
                        .with(jwt().jwt(c -> c.claim("sub", "Artur"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(museumId)))
                .andExpect(jsonPath("$.title", Matchers.is("Третьяковка")))
                .andExpect(jsonPath("$.description", Matchers.containsString(
                        "Государственная Третьяковская галерея — российский государственный художественный музей в Москве")))
                .andExpect(jsonPath("$.photo", Matchers.startsWith("data:image/webp;base64")))
                .andExpect(jsonPath("$.geo.country.name", Matchers.is("Россия")));
    }

    @Test
    void getAllMuseums_ShouldReturnMuseumsFromGrpc() throws Exception {
        final AllMuseumsResponse response = loadProtoResponse(
                WIREMOCK_ROOT + MUSEUM_RESPONSE_PATH + "all_museums_response.json",
                AllMuseumsResponse::newBuilder
        );

        mockMuseumService.stubFor(
                WireMockGrpc.method("GetAllMuseums")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(get("/api/museum")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", Matchers.is(museumId)))
                .andExpect(jsonPath("$.content[0].title", Matchers.is("Третьяковка")))
                .andExpect(jsonPath("$.content[1].id", Matchers.is("19bbbbb8-b687-4eec-8ba0-c8917c0a58a3")))
                .andExpect(jsonPath("$.content[1].title", Matchers.is("Лувр")));
    }

    @Test
    void searchMuseumsByTitle_ShouldReturnMuseumsFromGrpc() throws Exception {
        final AllMuseumsResponse response = loadProtoResponse(
                WIREMOCK_ROOT + MUSEUM_RESPONSE_PATH + "search_museums_response.json",
                AllMuseumsResponse::newBuilder
        );

        mockMuseumService.stubFor(
                WireMockGrpc.method("SearchMuseumsByTitle")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(get("/api/museum")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("title", "Треть")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title", Matchers.is("Третьяковка")));
    }

    @Test
    void createMuseum_ShouldReturnCreatedMuseum() throws Exception {
        final MuseumResponse response = loadProtoResponse(
                WIREMOCK_ROOT + MUSEUM_RESPONSE_PATH + "create_museum_response.json",
                MuseumResponse::newBuilder
        );

        final String requestJson = loadRequestJson(WIREMOCK_ROOT + MUSEUM_REQUEST_PATH + "create_museum_request.json");

        mockMuseumService.stubFor(
                WireMockGrpc.method("CreateMuseum")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(post("/api/museum")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.is(museumId)))
                .andExpect(jsonPath("$.title", Matchers.is("Новый музей")))
                .andExpect(jsonPath("$.description", Matchers.is("Описание нового музея")));
    }

    @Test
    void updateMuseum_ShouldReturnUpdatedMuseum() throws Exception {
        final MuseumResponse response = loadProtoResponse(
                WIREMOCK_ROOT + MUSEUM_RESPONSE_PATH + "update_museum_response.json",
                MuseumResponse::newBuilder
        );

        final String requestJson = loadRequestJson(WIREMOCK_ROOT + MUSEUM_REQUEST_PATH + "update_museum_request.json");

        mockMuseumService.stubFor(
                WireMockGrpc.method("UpdateMuseum")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(patch("/api/museum")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(museumId)))
                .andExpect(jsonPath("$.title", Matchers.is("Обновленный музей")))
                .andExpect(jsonPath("$.description", Matchers.is("Описание обновленного музея")));
    }

    @Test
    void getMuseumById_ShouldReturn404WhenMuseumNotFound() throws Exception {
        mockMuseumService.stubFor(
                WireMockGrpc.method("GetMuseum")
                        .willReturn(NOT_FOUND, "Музей с ID " + museumId + " не найден"));

        mockMvc.perform(get("/api/museum/{id}", museumId)
                        .with(jwt().jwt(c -> c.claim("sub", "Artur"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code", Matchers.is("404 NOT_FOUND")))
                .andExpect(jsonPath("$.error.errors[0].domain",
                        Matchers.is("/api/museum/" + museumId)))
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Музей с ID " + museumId + " не найден")));
    }

    @Test
    void getMuseumById_ShouldReturn400WhenInvalidIdFormat() throws Exception {
        mockMvc.perform(get("/api/museum/{id}", "invalid-uuid")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code", Matchers.is("400 BAD_REQUEST")))
                .andExpect(jsonPath("$.error.errors[0].domain",
                        Matchers.is("/api/museum/invalid-uuid")))
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.containsString("Неверный формат ID музея")));
    }

    @Test
    void createMuseum_ShouldReturn409WhenMuseumExists() throws Exception {
        final String requestJson = loadRequestJson(WIREMOCK_ROOT + MUSEUM_REQUEST_PATH + "create_museum_request.json");

        mockMuseumService.stubFor(
                WireMockGrpc.method("CreateMuseum")
                        .willReturn(ALREADY_EXISTS, "Музей уже существует"));

        mockMvc.perform(post("/api/museum")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code", Matchers.is("409 CONFLICT")))
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Создание музея с такими параметрами уже существует: Новый музей")));
    }

    @Test
    void updateMuseum_ShouldReturn404WhenMuseumNotFound() throws Exception {
        final String requestJson = loadRequestJson(WIREMOCK_ROOT + MUSEUM_REQUEST_PATH + "update_museum_request.json");

        mockMuseumService.stubFor(
                WireMockGrpc.method("UpdateMuseum")
                        .willReturn(NOT_FOUND, "Музей не найден"));

        mockMvc.perform(patch("/api/museum")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code", Matchers.is("404 NOT_FOUND")))
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Обновление музея с ID " + museumId + " не найден")));
    }

    @Test
    void searchMuseumsByTitle_ShouldReturn400WhenTitleIsBlank() throws Exception {
        mockMvc.perform(get("/api/museum")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("title", "")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code", Matchers.is("400 BAD_REQUEST")));
    }

    @Test
    void getAllMuseums_ShouldReturn503WhenGrpcServiceUnavailable() throws Exception {
        mockMuseumService.stubFor(
                WireMockGrpc.method("GetAllMuseums")
                        .willReturn(UNAVAILABLE, "Сервис недоступен"));

        mockMvc.perform(get("/api/museum")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Сервис временно недоступен")));
    }

    @Test
    void getMuseumById_ShouldReturn504WhenGrpcTimeout() throws Exception {
        mockMuseumService.stubFor(
                WireMockGrpc.method("GetMuseum")
                        .willReturn(DEADLINE_EXCEEDED, "Таймаут"));

        mockMvc.perform(get("/api/museum/{id}", museumId)
                        .with(jwt().jwt(c -> c.claim("sub", "Artur"))))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Превышено время ожидания ответа от сервиса")));
    }

    @Test
    void getAllMuseums_ShouldReturn500WhenUnexpectedGrpcError() throws Exception {
        mockMuseumService.stubFor(
                WireMockGrpc.method("GetAllMuseums")
                        .willReturn(INTERNAL, "Внутренняя ошибка"));

        mockMvc.perform(get("/api/museum")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.containsString("Произошла внутренняя ошибка сервера")));
    }

    @Test
    void getAllMuseums_ShouldReturn401WhenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/museum")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isUnauthorized());
    }
}