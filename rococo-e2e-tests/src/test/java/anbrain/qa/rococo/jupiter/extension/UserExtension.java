package anbrain.qa.rococo.jupiter.extension;

import anbrain.qa.rococo.jupiter.annotation.User;
import anbrain.qa.rococo.model.rest.UserJson;
import anbrain.qa.rococo.service.grpc.UserdataGrpcClient;
import anbrain.qa.rococo.service.kafka.KafkaService;
import anbrain.qa.rococo.service.rest.AuthRestClient;
import anbrain.qa.rococo.utils.RandomDataUtils;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;


public class UserExtension implements BeforeEachCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private static final String defaultPassword = "12345";

    private final AuthRestClient authRestClient = new AuthRestClient();
    private final UserdataGrpcClient userdataGrpcClient = new UserdataGrpcClient();

    @Override
    public void beforeEach(@NonNull ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if ("".equals(userAnno.username())) {
                        final String username = RandomDataUtils.randomUsername();

                        authRestClient.register(username, defaultPassword, defaultPassword);

                        try {
                            KafkaService.getUserJson(username);
                        } catch (InterruptedException e) {
                            throw new RuntimeException("UserExtension - ошибка чтения пользователя из Kafka: ",e);
                        }

                        UserJson user = userdataGrpcClient.getUser(username);
                        if (user == null) {
                            Assertions.fail("UserExtension - Пользователь не найден сервисом Userdata");
                        }
                        UserJson updateUser = userdataGrpcClient.updateUser(new UserJson(
                                user.id(),
                                user.username(),
                                userAnno.firstName().isEmpty() ? RandomDataUtils.randomFirstName() : userAnno.firstName(),
                                userAnno.lastName().isEmpty() ? RandomDataUtils.randomLastName() : userAnno.lastName(),
                                userAnno.avatar().isEmpty() ? RandomDataUtils.avatar() : userAnno.avatar()
                        ));

                        setUser(updateUser);
                    }
                });
    }

    @Override
    public boolean supportsParameter(@NonNull ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdUser();
    }

    public static UserJson createdUser() {
        final ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(
                context.getUniqueId(),
                UserJson.class
        );
    }

    public static void setUser(UserJson testUser) {
        final ExtensionContext context = TestMethodContextExtension.context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                testUser
        );
    }
}