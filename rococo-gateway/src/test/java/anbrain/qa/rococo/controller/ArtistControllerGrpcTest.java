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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpc;
import org.wiremock.grpc.dsl.WireMockGrpcService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ArtistControllerGrpcTest {

    private static final int WIREMOCK_PORT = 5002;
    private static final String GRPC_SERVICE_NAME = "anbrain.qa.rococo.grpc.ArtistService";
    private static final String WIREMOCK_ROOT = "src/test/resources/wiremock";

    private final WireMockServer wm = new WireMockServer(
            WireMockConfiguration.wireMockConfig()
                    .port(WIREMOCK_PORT)
                    .withRootDirectory(WIREMOCK_ROOT)
                    .extensions(new Jetty12GrpcExtensionFactory())
    );

    private final WireMockGrpcService mockArtistService = new WireMockGrpcService(
            WireMock.create().port(WIREMOCK_PORT).build(),
            GRPC_SERVICE_NAME
    );

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        wm.start();
        System.out.println("WireMock GRPC запущен на порту: " + wm.port());
    }

    @AfterEach
    void afterEach() {
        wm.stop();
    }

    @Test
    void getAllArtists_ShouldReturnArtistsFromGrpc() throws Exception {
        mockArtistService.stubFor(
                WireMockGrpc.method("GetAllArtists")
                        .willReturn(WireMockGrpc.message(
                                AllArtistsResponse.newBuilder()
                                        .addArtists(ArtistResponse.newBuilder()
                                                .setId("550e8400-e29b-41d4-a716-446655440000")
                                                .setName("Van Gogh")
                                                .build())
                                        .build()
                        )));

        // Выполнение запроса и проверки
        mockMvc.perform(get("/api/artist")
                        .with(jwt().jwt(c -> c.claim("sub", "duck")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", Matchers.is("Van Gogh")));
    }
}
