package anbrain.qa.rococo.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpcService;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext
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

    public static final int USERDATA_WIREMOCK_PORT = 9090;
    public static final String USERDATA_GRPC_SERVICE_NAME = "Userdata";
    public static final String USERDATA__REQUEST_PATH = "/request/userdata/";
    public static final String USERDATA__RESPONSE_PATH = "/response/userdata/";

    public static WireMockServer artistWm;
    public static WireMockServer museumWm;
    public static WireMockServer paintingWm;
    public static WireMockServer userdataWm;

    public static WireMockGrpcService mockPaintingService;
    public static WireMockGrpcService mockArtistService;
    public static WireMockGrpcService mockMuseumService;
    public static WireMockGrpcService mockCountryService;
    public static WireMockGrpcService mockUserdataService;

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

        paintingWm = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .port(PAINTING_WIREMOCK_PORT)
                        .withRootDirectory(WIREMOCK_ROOT)
                        .extensions(new Jetty12GrpcExtensionFactory())
        );
        paintingWm.start();
        System.out.println("Painting server started on port " + paintingWm.port());

        userdataWm = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .port(USERDATA_WIREMOCK_PORT)
                        .withRootDirectory(WIREMOCK_ROOT)
                        .extensions(new Jetty12GrpcExtensionFactory())
        );
        userdataWm.start();
        System.out.println("Userdata server started on port " + userdataWm.port());
    }

    @BeforeEach
    void beforeEach(){
        mockPaintingService = new WireMockGrpcService(
                WireMock.create().port(PAINTING_WIREMOCK_PORT).build(),
                PAINTING_GRPC_SERVICE_NAME
        );

        mockArtistService = new WireMockGrpcService(
                WireMock.create().port(ARTIST_WIREMOCK_PORT).build(),
                ARTIST_GRPC_SERVICE_NAME
        );

        mockMuseumService = new WireMockGrpcService(
                WireMock.create().port(MUSEUM_WIREMOCK_PORT).build(),
                MUSEUM_GRPC_SERVICE_NAME
        );

        mockCountryService = new WireMockGrpcService(
                WireMock.create().port(MUSEUM_WIREMOCK_PORT).build(),
                COUNTRY_GRPC_SERVICE_NAME
        );

        mockUserdataService = new WireMockGrpcService(
                WireMock.create().port(USERDATA_WIREMOCK_PORT).build(),
                USERDATA_GRPC_SERVICE_NAME
        );
    }

    @AfterEach
    void afterEach() {
        mockArtistService.resetAll();
        mockMuseumService.resetAll();
        mockCountryService.resetAll();
        mockPaintingService.resetAll();
        mockUserdataService.resetAll();
    }

    @AfterAll
    public static void afterAll() {
        artistWm.stop();
        museumWm.stop();
        paintingWm.stop();
        userdataWm.stop();
    }

}
