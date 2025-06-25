package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.grpc.AllArtistsResponse;
import anbrain.qa.rococo.grpc.ArtistResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpc;
import org.wiremock.grpc.dsl.WireMockGrpcService;


import static anbrain.qa.rococo.utils.ContractTestGrpcUtils.loadProtoResponse;
import static anbrain.qa.rococo.utils.ContractTestGrpcUtils.loadRequestJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ArtistControllerGrpcTest {

    private static final int WIREMOCK_PORT = 9091;
    private static final String GRPC_SERVICE_NAME = "anbrain.qa.rococo.grpc.ArtistService";
    private static final String WIREMOCK_ROOT = "src/test/resources/wiremock";
    private static final String REQUEST_PATH = "/request/artist/";
    private static final String RESPONSE_PATH = "/response/artist/";
    private final String artistID = "104f76ce-0508-49d4-8967-fdf1ebb8cf45";

    private WireMockServer wm;
    private WireMockGrpcService mockArtistService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        wm = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .port(WIREMOCK_PORT)
                        .withRootDirectory(WIREMOCK_ROOT)
                        .extensions(new Jetty12GrpcExtensionFactory())
        );
        wm.start();
        wm.resetAll();

        mockArtistService = new WireMockGrpcService(
                WireMock.create().port(WIREMOCK_PORT).build(),
                GRPC_SERVICE_NAME
        );

        mockArtistService.resetAll();
        System.out.println("server start in "+wm.port());
    }

    @AfterEach
    void afterEach() {
        System.out.println("Stopping WireMock server...");
        if (wm != null) {
            if (wm.isRunning()) {
                wm.stop();
                System.out.println("WireMock server stopped.");
            } else {
                System.out.println("WireMock server was already stopped.");
            }
        }
    }

    @Test
    void getAllArtists_ShouldReturnArtistsFromGrpc() throws Exception {

        final AllArtistsResponse response = loadProtoResponse(
                WIREMOCK_ROOT+RESPONSE_PATH+"all_artist_response.json",
                AllArtistsResponse::newBuilder
        );

        mockArtistService.stubFor(
                WireMockGrpc.method("GetAllArtists")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(get("/api/artist")
                        .with(jwt().jwt(c -> c.claim("sub", "duck")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", Matchers.is("104f76ce-0508-49d4-8967-fdf1ebb8cf45")))
                .andExpect(jsonPath("$.content[0].name", Matchers.is("Шишкин")))
                .andExpect(jsonPath("$.content[1].id", Matchers.is("19bbbbb8-b687-4eec-8ba0-c8917c0a58a3")))
                .andExpect(jsonPath("$.content[1].name", Matchers.is("Ренуар")))
                .andExpect(jsonPath("$.content[2].id", Matchers.is("5a486b2f-c361-459e-bd3f-60692a635ea9")))
                .andExpect(jsonPath("$.content[2].name", Matchers.is("Левитан")));
    }

    @Test
    void getArtistById_ShouldReturnArtistFromGrpc() throws Exception {

        final ArtistResponse response = loadProtoResponse(
                WIREMOCK_ROOT+RESPONSE_PATH+"artist_response.json",
                ArtistResponse::newBuilder
        );

        mockArtistService.stubFor(
                WireMockGrpc.method("GetArtist")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(get("/api/artist/{id}", artistID)
                        .with(jwt().jwt(c -> c.claim("sub", "duck"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(artistID)))
                .andExpect(jsonPath("$.name", Matchers.is("Шишкин")))
                .andExpect(jsonPath("$.biography", Matchers.is(
                        "Русский живописец, рисовальщик и гравёр-офортист, один из главных мастеров реалистического пейзажа второй половины XIX века")))
                .andExpect(jsonPath("$.photo", Matchers.startsWith("data:image/jpeg;base64")));
    }

    @Test
    void searchArtistsByName_ShouldReturnArtistsFromGrpc() throws Exception {

        final AllArtistsResponse response = loadProtoResponse(
                WIREMOCK_ROOT+RESPONSE_PATH+"search_artists_response.json",
                AllArtistsResponse::newBuilder
        );

        mockArtistService.stubFor(
                WireMockGrpc.method("SearchArtistsByName")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(get("/api/artist")
                        .with(jwt().jwt(c -> c.claim("sub", "duck")))
                        .param("name", "Шиш")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", Matchers.is("Шишкин")));
    }

    @Test
    void createArtist_ShouldReturnCreatedArtist() throws Exception {

        final ArtistResponse response = loadProtoResponse(
                WIREMOCK_ROOT+RESPONSE_PATH+"create_artist_response.json",
                ArtistResponse::newBuilder
        );

        final String requestJson = loadRequestJson(WIREMOCK_ROOT+REQUEST_PATH+"create_artist_request.json");

        mockArtistService.stubFor(
                WireMockGrpc.method("CreateArtist")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(post("/api/artist")
                        .with(jwt().jwt(c -> c.claim("sub", "duck")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.is(artistID)))
                .andExpect(jsonPath("$.biography", Matchers.is("Биография нового художника")))
                .andExpect(jsonPath("$.name", Matchers.is("Новый художник")));
    }

    @Test
    void updateArtist_ShouldReturnUpdatedArtist() throws Exception {

        final ArtistResponse response = loadProtoResponse(
                WIREMOCK_ROOT+RESPONSE_PATH+"update_artist_response.json",
                ArtistResponse::newBuilder
        );

        final String requestJson = loadRequestJson(WIREMOCK_ROOT+REQUEST_PATH+"update_artist_request.json");

        mockArtistService.stubFor(
                WireMockGrpc.method("UpdateArtist")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(put("/api/artist")
                .with(jwt().jwt(c -> c.claim("sub", "duck")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is("104f76ce-0508-49d4-8967-fdf1ebb8cf45")))
                .andExpect(jsonPath("$.biography", Matchers.is("Обновленная биография")))
                .andExpect(jsonPath("$.name", Matchers.is("Обновленный Шишкин")));
    }
}
