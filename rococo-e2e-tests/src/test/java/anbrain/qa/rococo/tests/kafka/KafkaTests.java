package anbrain.qa.rococo.tests.kafka;

import anbrain.qa.rococo.jupiter.annotation.meta.KafkaTest;
import anbrain.qa.rococo.model.rest.UserJson;
import anbrain.qa.rococo.service.grpc.UserdataGrpcClient;
import anbrain.qa.rococo.service.kafka.KafkaService;
import anbrain.qa.rococo.service.rest.AuthRestClient;
import anbrain.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@KafkaTest
@DisplayName("[Kafka] Проверка работы интеграции с Kafka")
public class KafkaTests {
    private final String username = RandomDataUtils.randomUsername();
    private final String defaultPassword = "12345";

    private final AuthRestClient authRestClient = new AuthRestClient();
    private final UserdataGrpcClient userdataGrpcClient = new UserdataGrpcClient();

    @Test
    @DisplayName("Должно отправиться сообщение в Kafka после регистрации пользователя")
    void shouldSendMessageInKafkaAfterRegistrationUser() throws InterruptedException {

        authRestClient.register(username, defaultPassword, defaultPassword);

        UserJson userFromKafka = KafkaService.getUserJson(username);

        Assertions.assertEquals(
                username,
                userFromKafka.username()
        );
    }

    @Test
    @DisplayName("Должен быть создан пользователь в сервисе Userdata после прочтения сообщения в Kafka")
    void shouldBeCreatedUserInServiceUserdataAfterReadMessageFromKafka() throws InterruptedException {

        authRestClient.register(username, defaultPassword, defaultPassword);

        UserJson userFromKafka = KafkaService.getUserJson(username);
        UserJson userFromUserdata = userdataGrpcClient.getUser(username);

        Assertions.assertEquals(
                userFromUserdata.username(),
                userFromKafka.username()
        );
    }
}
