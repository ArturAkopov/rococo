package anbrain.qa.rococo.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BaseControllerTest {

    public static final String WIREMOCK_ROOT = "src/test/resources/wiremock";

    public static final int ARTIST_WIREMOCK_PORT = 9091;
    public static final String ARTIST_GRPC_SERVICE_NAME = "anbrain.qa.rococo.grpc.ArtistService";
    public static final String ARTIST_REQUEST_PATH = "/request/artist/";
    public static final String ARTIST_RESPONSE_PATH = "/response/artist/";

    public static final int MUSEUM_WIREMOCK_PORT = 9093;
    public static final String MUSEUM_GRPC_SERVICE_NAME = "anbrain.qa.rococo.grpc.MuseumService";
    public static final String MUSEUM_REQUEST_PATH = "/request/museum/";
    public static final String MUSEUM_RESPONSE_PATH = "/response/museum/";
    public static final String COUNTRY_GRPC_SERVICE_NAME = "anbrain.qa.rococo.grpc.CountryService";
    public static final String COUNTRY_RESPONSE_PATH = "/response/country/";

    public static final int PAINTING_WIREMOCK_PORT = 9094;
    public static final String PAINTING_GRPC_SERVICE_NAME = "anbrain.qa.rococo.grpc.PaintingService";
    public static final String PAINTING_REQUEST_PATH = "/request/painting/";
    public static final String PAINTING_RESPONSE_PATH = "/response/painting/";

    public static WireMockServer artistWm;
    public static WireMockServer museumWm;
    public static WireMockServer paintingWm;

    @Autowired
    public MockMvc mockMvc;

    @BeforeAll
    public static void beforeAll() {
        artistWm = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .port(ARTIST_WIREMOCK_PORT)
                        .withRootDirectory(WIREMOCK_ROOT)
                        .extensions(new Jetty12GrpcExtensionFactory())
        );
        artistWm.start();
        System.out.println("Artist server started on port " + artistWm.port());

        museumWm = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .port(MUSEUM_WIREMOCK_PORT)
                        .withRootDirectory(WIREMOCK_ROOT)
                        .extensions(new Jetty12GrpcExtensionFactory())
        );
        museumWm.start();
        System.out.println("Museum server started on port " + museumWm.port());
        System.out.println("Country server started on port " + museumWm.port());

        paintingWm = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .port(PAINTING_WIREMOCK_PORT)
                        .withRootDirectory(WIREMOCK_ROOT)
                        .extensions(new Jetty12GrpcExtensionFactory())
        );
        paintingWm.start();
        System.out.println("Painting server started on port " + paintingWm.port());
    }

    @AfterAll
    public static void afterAll() {
        artistWm.stop();
        museumWm.stop();
        paintingWm.stop();
    }

}
