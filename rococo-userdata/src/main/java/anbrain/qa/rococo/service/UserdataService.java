package anbrain.qa.rococo.service;

import anbrain.qa.rococo.grpc.UpdateUserRequest;
import anbrain.qa.rococo.model.UserJson;

public interface UserdataService {

    UserJson getUser(String username);

    UserJson updateUser(UpdateUserRequest userRequest);

}
