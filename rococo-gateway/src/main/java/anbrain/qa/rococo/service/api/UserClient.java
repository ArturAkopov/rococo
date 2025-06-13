package anbrain.qa.rococo.service.api;

import anbrain.qa.rococo.model.UserJson;
import anbrain.qa.rococo.service.grpc.UserGrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserClient {

    private final UserGrpcClient userGrpcClient;

    @Autowired
    public UserClient(UserGrpcClient userGrpcClient) {
        this.userGrpcClient = userGrpcClient;
    }

    public UserJson getUser(String username) {
        return userGrpcClient.getUser(username);
    }

    public UserJson updateUser(String username, UserJson updateRequest) {
        return userGrpcClient.updateUser(username, updateRequest);
    }
}