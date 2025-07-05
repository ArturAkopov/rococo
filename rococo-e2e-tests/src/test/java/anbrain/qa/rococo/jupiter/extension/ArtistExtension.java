package anbrain.qa.rococo.jupiter.extension;

import anbrain.qa.rococo.jupiter.annotation.Artist;
import anbrain.qa.rococo.model.rest.ArtistJson;
import anbrain.qa.rococo.service.grpc.ArtistGrpcClient;
import anbrain.qa.rococo.utils.RandomDataUtils;
import lombok.NonNull;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class ArtistExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);
    private final ArtistGrpcClient artistGrpcClient = new ArtistGrpcClient();

    @Override
    public void beforeEach(@NonNull ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Artist.class)
                .ifPresent(artistAnno -> {

                    String name = artistAnno.name().isEmpty()
                            ? RandomDataUtils.randomArtistName()
                            : artistAnno.name();

                    ArtistJson artistJson = artistGrpcClient.searchArtistsByName(name);

                    if (artistJson == null) {
                        artistJson = artistGrpcClient.createArtist(
                                createArtistJson(name, artistAnno.biography())
                        );
                    }

                    setArtist(artistJson);
                });
    }

    @Override
    public boolean supportsParameter(@NonNull ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(ArtistJson.class);
    }

    @Override
    public ArtistJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdArtist();
    }

    public static ArtistJson createdArtist() {
        final ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(
                context.getUniqueId(),
                ArtistJson.class
        );
    }

    public static void setArtist(ArtistJson testArtist) {
        final ExtensionContext context = TestMethodContextExtension.context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                testArtist
        );
    }

    @NonNull
    private ArtistJson createArtistJson(@NonNull String name, @NonNull String biography) {
        return new ArtistJson(
                null,
                name.isEmpty() ? RandomDataUtils.randomArtistName() : name,
                biography.isEmpty() ? RandomDataUtils.randomString() : biography,
                RandomDataUtils.avatar()
        );
    }
}
