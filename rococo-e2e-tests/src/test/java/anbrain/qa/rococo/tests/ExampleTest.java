package anbrain.qa.rococo.tests;

import anbrain.qa.rococo.jupiter.annotation.Artist;
import anbrain.qa.rococo.jupiter.annotation.Museum;
import anbrain.qa.rococo.jupiter.annotation.Painting;
import anbrain.qa.rococo.jupiter.extension.*;
import anbrain.qa.rococo.model.rest.PaintingJson;
import anbrain.qa.rococo.service.rest.AuthRestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestMethodContextExtension.class)
@ExtendWith(UserExtension.class)
@ExtendWith(ArtistExtension.class)
@ExtendWith(MuseumExtension.class)
@ExtendWith(PaintingExtension.class)
@ExtendWith(ApiLoginExtension.class)
public class ExampleTest {

    @Artist
    @Museum
    @Painting
    @Test
    void getGrpcUser(PaintingJson museumJson) {
        System.out.println(museumJson);
    }


    @Artist
    @Museum
    @Painting(
            title = "TEST PAINTING",
            description = "TEST DESCRIPTION"
    )
    @Test
    void fakerAvatar(PaintingJson museumJson) {
        System.out.println(museumJson);
    }

    @Test
    void register() {
        AuthRestClient authRestClient = new AuthRestClient();

//        authRestClient.register("Artur1","12345","12345");

        authRestClient.login("Artur1","12345");
    }

}