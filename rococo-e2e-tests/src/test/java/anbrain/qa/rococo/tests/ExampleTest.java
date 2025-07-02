package anbrain.qa.rococo.tests;

import anbrain.qa.rococo.jupiter.annotation.User;
import anbrain.qa.rococo.jupiter.extension.TestMethodContextExtension;
import anbrain.qa.rococo.jupiter.extension.UserExtension;
import anbrain.qa.rococo.model.rest.UserJson;
import anbrain.qa.rococo.service.grpc.UserdataGrpcClient;
import anbrain.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestMethodContextExtension.class)
@ExtendWith(UserExtension.class)
public class ExampleTest {

    @Test
    void getGrpcUser(){
        UserdataGrpcClient userdataGrpcClient = new UserdataGrpcClient();
        UserJson userJson = userdataGrpcClient.getUser("testuser");
        System.out.println(userJson);
    }

    @Test
    void fakerAvatar(){
                System.out.println(RandomDataUtils.avatar());
    }

    @User
    @Test
    void userTest(UserJson userJson){
        System.out.println(userJson);
    }

}
