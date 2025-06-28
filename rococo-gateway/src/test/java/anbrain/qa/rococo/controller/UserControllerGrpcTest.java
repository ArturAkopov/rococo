package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.grpc.UserResponse;
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
public class UserControllerGrpcTest extends BaseControllerTest{

    private final String testUsername = "Testuser";

    @Test
    void getUser_ShouldReturnUserFromGrpc() throws Exception {
        final UserResponse response = loadProtoResponse(
                WIREMOCK_ROOT + USERDATA__RESPONSE_PATH + "user_response.json",
                UserResponse::newBuilder
        );

        mockUserdataService.stubFor(
                WireMockGrpc.method("GetUser")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(get("/api/user")
                        .with(jwt().jwt(c -> c.claim("sub", testUsername))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", Matchers.is(testUsername)))
                .andExpect(jsonPath("$.firstname", Matchers.is("Firstname")))
                .andExpect(jsonPath("$.lastname", Matchers.is("Lastname")))
                .andExpect(jsonPath("$.avatar", Matchers.startsWith("data:image/jpeg;base64")));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        final UserResponse response = loadProtoResponse(
                WIREMOCK_ROOT + USERDATA__RESPONSE_PATH + "update_user_response.json",
                UserResponse::newBuilder
        );

        final String requestJson = loadRequestJson(WIREMOCK_ROOT + USERDATA__REQUEST_PATH + "update_user_request.json");

        mockUserdataService.stubFor(
                WireMockGrpc.method("UpdateUser")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(patch("/api/user")
                        .with(jwt().jwt(c -> c.claim("sub", testUsername)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", Matchers.is(testUsername)))
                .andExpect(jsonPath("$.firstname", Matchers.is("UpdatedFirstname")))
                .andExpect(jsonPath("$.lastname", Matchers.is("UpdatedLastname")));
    }

    @Test
    void getUser_ShouldReturn404WhenUserNotFound() throws Exception {
        mockUserdataService.stubFor(
                WireMockGrpc.method("GetUser")
                        .willReturn(NOT_FOUND, "Пользователь " + testUsername + " не найден"));

        mockMvc.perform(get("/api/user")
                        .with(jwt().jwt(c -> c.claim("sub", testUsername))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code", Matchers.is("404 NOT_FOUND")))
                .andExpect(jsonPath("$.error.errors[0].domain",
                        Matchers.is("/api/user")))
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Пользователь с ID " + testUsername + " не найден")));
    }

    @Test
    void updateUser_ShouldReturn400WhenInvalidRequest() throws Exception {
        final String requestJson = "{\"firstname\":\"\", \"lastname\":\"\"}";

        mockMvc.perform(patch("/api/user")
                        .with(jwt().jwt(c -> c.claim("sub", testUsername)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code", Matchers.is("400 BAD_REQUEST")))
                .andExpect(jsonPath("$.error.errors[0].domain",
                        Matchers.is("/api/user")))
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.containsString("Имя пользователя не может быть пустым")));
    }

    @Test
    void updateUser_ShouldReturn404WhenUserNotFound() throws Exception {
        final String requestJson = loadRequestJson(WIREMOCK_ROOT + USERDATA__REQUEST_PATH + "update_user_request.json");

        mockUserdataService.stubFor(
                WireMockGrpc.method("UpdateUser")
                        .willReturn(NOT_FOUND, "Пользователь не найден"));

        mockMvc.perform(patch("/api/user")
                        .with(jwt().jwt(c -> c.claim("sub", testUsername)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code", Matchers.is("404 NOT_FOUND")))
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Обновление пользователя с ID " + testUsername + " не найден")));
    }

    @Test
    void getUser_ShouldReturn503WhenGrpcServiceUnavailable() throws Exception {
        mockUserdataService.stubFor(
                WireMockGrpc.method("GetUser")
                        .willReturn(UNAVAILABLE, "Сервис недоступен"));

        mockMvc.perform(get("/api/user")
                        .with(jwt().jwt(c -> c.claim("sub", testUsername))))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Сервис временно недоступен")));
    }

    @Test
    void updateUser_ShouldReturn504WhenGrpcTimeout() throws Exception {
        final String requestJson = loadRequestJson(WIREMOCK_ROOT + USERDATA__REQUEST_PATH + "update_user_request.json");

        mockUserdataService.stubFor(
                WireMockGrpc.method("UpdateUser")
                        .willReturn(DEADLINE_EXCEEDED, "Таймаут"));

        mockMvc.perform(patch("/api/user")
                        .with(jwt().jwt(c -> c.claim("sub", testUsername)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Превышено время ожидания ответа от сервиса")));
    }

    @Test
    void getUser_ShouldReturn500WhenUnexpectedGrpcError() throws Exception {
        mockUserdataService.stubFor(
                WireMockGrpc.method("GetUser")
                        .willReturn(INTERNAL, "Внутренняя ошибка"));

        mockMvc.perform(get("/api/user")
                        .with(jwt().jwt(c -> c.claim("sub", testUsername))))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.containsString("Произошла внутренняя ошибка сервера")));
    }

    @Test
    void getUser_ShouldReturn401WhenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUser_ShouldReturn401WhenUnauthorized() throws Exception {
        final String requestJson = loadRequestJson(WIREMOCK_ROOT + USERDATA__REQUEST_PATH + "update_user_request.json");

        mockMvc.perform(patch("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isForbidden());
    }
}