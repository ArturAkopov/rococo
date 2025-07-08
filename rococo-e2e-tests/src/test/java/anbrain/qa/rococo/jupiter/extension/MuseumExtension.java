package anbrain.qa.rococo.jupiter.extension;

import anbrain.qa.rococo.jupiter.annotation.Museum;
import anbrain.qa.rococo.model.rest.CountryJson;
import anbrain.qa.rococo.model.rest.GeoJson;
import anbrain.qa.rococo.model.rest.MuseumJson;
import anbrain.qa.rococo.service.grpc.MuseumGrpcClient;
import anbrain.qa.rococo.utils.RandomDataUtils;
import lombok.NonNull;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class MuseumExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);
    private final MuseumGrpcClient museumGrpcClient = new MuseumGrpcClient();

    @Override
    public void beforeEach(@NonNull ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Museum.class)
                .ifPresent(museumAnno -> {

                    String title = museumAnno.title().isEmpty()
                            ? RandomDataUtils.randomMuseumTitle()
                            : museumAnno.title();

                    MuseumJson museumJson = museumGrpcClient.searchMuseumsByTitle(title);

                    if (museumJson == null) {
                        CountryJson countryJson = resolveCountry(museumAnno);
                        museumJson = museumGrpcClient.createMuseum(
                                createMuseumJson(
                                        title,
                                        museumAnno.description(),
                                        museumAnno.city(),
                                        countryJson
                                )
                        );
                    }

                    setMuseum(museumJson);
                });
    }

    @Override
    public boolean supportsParameter(@NonNull ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(MuseumJson.class);
    }

    @Override
    public MuseumJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdMuseum();
    }

    public static MuseumJson createdMuseum() {
        final ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(
                context.getUniqueId(),
                MuseumJson.class
        );
    }

    public static void setMuseum(MuseumJson museumJson) {
        final ExtensionContext context = TestMethodContextExtension.context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                museumJson
        );
    }

    @NonNull
    private MuseumJson createMuseumJson(@NonNull String title, @NonNull String description, @NonNull String city, @NonNull CountryJson country) {


        return new MuseumJson(
                null,
                title.isEmpty() ? RandomDataUtils.randomMuseumTitle() : title,
                description.isEmpty() ? RandomDataUtils.randomString() : description,
                RandomDataUtils.avatar(),
                new GeoJson(
                        city.isEmpty() ? RandomDataUtils.randomCity() : city,
                        country
                )
        );
    }

    private CountryJson resolveCountry(@NonNull Museum museumAnno) {

        if (!museumAnno.country().isEmpty()) {
            CountryJson country = museumGrpcClient.getCountryByName(0, 10, museumAnno.country());
            if (country != null) {
                return country;
            }
        }

        return museumGrpcClient.getRandomCountry(0, 20);
    }
}
