package anbrain.qa.rococo.jupiter.extension;

import anbrain.qa.rococo.jupiter.annotation.Painting;
import anbrain.qa.rococo.model.rest.ArtistJson;
import anbrain.qa.rococo.model.rest.MuseumJson;
import anbrain.qa.rococo.model.rest.PaintingJson;
import anbrain.qa.rococo.service.grpc.PaintingGrpcClient;
import anbrain.qa.rococo.utils.RandomDataUtils;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.UUID;

public class PaintingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PaintingExtension.class);
    private final PaintingGrpcClient paintingGrpcClient = new PaintingGrpcClient();

    @Override
    public void beforeEach(@NonNull ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Painting.class)
                .ifPresent(paintingAnno -> {

                    String title = paintingAnno.title().isEmpty()
                            ? RandomDataUtils.randomPaintingTitle()
                            : paintingAnno.title();

                    PaintingJson paintingJson = paintingGrpcClient.searchPaintingsByTitle(title);

                    if (paintingJson == null) {
                        MuseumJson museumJson = MuseumExtension.createdMuseum();
                        ArtistJson artistJson = ArtistExtension.createdArtist();
                        if (museumJson == null || artistJson == null) {
                            Assertions.fail("PaintingExtension не предоставлена информация о связанном @Museum или @Artist");
                        }
                        paintingJson = paintingGrpcClient.createPainting(
                                createPaintingJson(
                                        title,
                                        paintingAnno.description(),
                                        museumJson.id(),
                                        artistJson.id()
                                        )
                        );
                    }

                    setPainting(paintingJson);
                });
    }

    @Override
    public boolean supportsParameter(@NonNull ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(PaintingJson.class);
    }

    @Override
    public PaintingJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdPainting();
    }

    public static PaintingJson createdPainting() {
        final ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(
                context.getUniqueId(),
                PaintingJson.class
        );
    }

    public static void setPainting(PaintingJson paintingJson) {
        final ExtensionContext context = TestMethodContextExtension.context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                paintingJson
        );
    }

    @NonNull
    private PaintingJson createPaintingJson(@NonNull String title, @NonNull String description, @NonNull UUID museumId, @NonNull UUID artistId) {

        return new PaintingJson(
                null,
                title.isEmpty() ? RandomDataUtils.randomPaintingTitle() : title,
                description.isEmpty() ? RandomDataUtils.randomString() : description,
                RandomDataUtils.avatar(),
                String.valueOf(museumId),
                String.valueOf(artistId)
        );
    }

}
