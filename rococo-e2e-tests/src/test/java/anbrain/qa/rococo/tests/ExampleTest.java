package anbrain.qa.rococo.tests;

import anbrain.qa.rococo.jupiter.annotation.Artist;
import anbrain.qa.rococo.jupiter.annotation.Museum;
import anbrain.qa.rococo.jupiter.annotation.Painting;
import anbrain.qa.rococo.jupiter.extension.*;
import anbrain.qa.rococo.model.rest.PaintingJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestMethodContextExtension.class)
@ExtendWith(UserExtension.class)
@ExtendWith(ArtistExtension.class)
@ExtendWith(MuseumExtension.class)
@ExtendWith(PaintingExtension.class)
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

}