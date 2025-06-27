package anbrain.qa.rococo.service;

import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.exception.*;
import anbrain.qa.rococo.model.UserJson;
import anbrain.qa.rococo.service.grpc.UserGrpcClient;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserGrpcClientTests {

    @Mock
    private UserdataGrpc.UserdataBlockingStub userServiceStub;

    @InjectMocks
    private UserGrpcClient userGrpcClient;

    @Captor
    private ArgumentCaptor<UserRequest> userRequestCaptor;
    @Captor
    private ArgumentCaptor<UpdateUserRequest> updateUserRequestCaptor;

    private final String testUsername = "testuser";
    private final UUID testId = UUID.randomUUID();
    private final String testFirstname = "Test";
    private final String testLastname = "User";
    private final String testAvatar = "avatar1";

    @Test
    void getUser_shouldReturnUserWhenFound() {
        UserResponse response = UserResponse.newBuilder()
                .setId(testId.toString())
                .setUsername(testUsername)
                .setFirstname(testFirstname)
                .setLastname(testLastname)
                .setAvatar(testAvatar)
                .build();

        when(userServiceStub.getUser(any(UserRequest.class))).thenReturn(response);

        UserJson result = userGrpcClient.getUser(testUsername);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testUsername, result.username());
        assertEquals(testFirstname, result.firstname());
        assertEquals(testLastname, result.lastname());
        assertEquals(testAvatar, result.avatar());

        verify(userServiceStub).getUser(userRequestCaptor.capture());
        assertEquals(testUsername, userRequestCaptor.getValue().getUsername());
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() {
        UserJson updateRequest = new UserJson(null, testUsername, testFirstname, testLastname, testAvatar);

        UserResponse response = UserResponse.newBuilder()
                .setId(testId.toString())
                .setUsername(testUsername)
                .setFirstname(testFirstname)
                .setLastname(testLastname)
                .setAvatar(testAvatar)
                .build();

        when(userServiceStub.updateUser(any(UpdateUserRequest.class))).thenReturn(response);

        UserJson result = userGrpcClient.updateUser(testUsername, updateRequest);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testUsername, result.username());
        assertEquals(testFirstname, result.firstname());
        assertEquals(testLastname, result.lastname());
        assertEquals(testAvatar, result.avatar());

        verify(userServiceStub).updateUser(updateUserRequestCaptor.capture());
        assertEquals(testUsername, updateUserRequestCaptor.getValue().getUsername());
        assertEquals(testFirstname, updateUserRequestCaptor.getValue().getFirstname());
        assertEquals(testLastname, updateUserRequestCaptor.getValue().getLastname());
        assertEquals(testAvatar, updateUserRequestCaptor.getValue().getAvatar());
    }

    @Test
    void getUser_shouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userServiceStub.getUser(any(UserRequest.class)))
                .thenReturn(UserResponse.newBuilder().build());

        RococoNotFoundException ex = assertThrows(RococoNotFoundException.class,
                () -> userGrpcClient.getUser(testUsername));
        assertEquals("Пользователь с ID testuser не найден", ex.getMessage());
    }

    @Test
    void getUser_shouldThrowServiceUnavailableExceptionWhenServiceUnavailable() {
        when(userServiceStub.getUser(any(UserRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));

        RococoServiceUnavailableException ex = assertThrows(RococoServiceUnavailableException.class,
                () -> userGrpcClient.getUser(testUsername));
        assertEquals("Сервис временно недоступен", ex.getMessage());
    }

    @Test
    void getUser_shouldThrowValidationExceptionWhenInvalidArgument() {
        when(userServiceStub.getUser(any(UserRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INVALID_ARGUMENT));

        RococoValidationException ex = assertThrows(RococoValidationException.class,
                () -> userGrpcClient.getUser(testUsername));
        assertEquals("Ошибка валидации данных: Пользователь - testuser", ex.getMessage());
    }

    @Test
    void updateUser_shouldThrowAccessDeniedExceptionWhenPermissionDenied() {
        UserJson updateRequest = new UserJson(null, testUsername, testFirstname, testLastname, testAvatar);

        when(userServiceStub.updateUser(any(UpdateUserRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.PERMISSION_DENIED));

        RococoAccessDeniedException ex = assertThrows(RococoAccessDeniedException.class,
                () -> userGrpcClient.updateUser(testUsername, updateRequest));
        assertEquals("Доступ запрещен", ex.getMessage());
    }

    @Test
    void updateUser_shouldThrowConflictExceptionWhenAlreadyExists() {
        UserJson updateRequest = new UserJson(null, testUsername, testFirstname, testLastname, testAvatar);

        when(userServiceStub.updateUser(any(UpdateUserRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.ALREADY_EXISTS));

        RococoConflictException ex = assertThrows(RococoConflictException.class,
                () -> userGrpcClient.updateUser(testUsername, updateRequest));
        assertEquals("Обновление пользователя с такими параметрами уже существует: testuser", ex.getMessage());
    }

    @Test
    void updateUser_shouldThrowTimeoutExceptionWhenDeadlineExceeded() {
        UserJson updateRequest = new UserJson(null, testUsername, testFirstname, testLastname, testAvatar);

        when(userServiceStub.updateUser(any(UpdateUserRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.DEADLINE_EXCEEDED));

        RococoServiceUnavailableException ex = assertThrows(RococoServiceUnavailableException.class,
                () -> userGrpcClient.updateUser(testUsername, updateRequest));
        assertEquals("Превышено время ожидания ответа от сервиса", ex.getMessage());
    }

    @Test
    void updateUser_shouldThrowRuntimeExceptionWhenUnknownError() {
        UserJson updateRequest = new UserJson(null, testUsername, testFirstname, testLastname, testAvatar);

        when(userServiceStub.updateUser(any(UpdateUserRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.UNKNOWN));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userGrpcClient.updateUser(testUsername, updateRequest));
        assertTrue(ex.getMessage().contains("Ошибка при обработке Обновление пользователя: testuser"));
    }

    @Test
    void convertToUserJson_shouldConvertResponseToUserJson() {
        UserResponse response = UserResponse.newBuilder()
                .setId(testId.toString())
                .setUsername(testUsername)
                .setFirstname(testFirstname)
                .setLastname(testLastname)
                .setAvatar(testAvatar)
                .build();

        UserJson result = userGrpcClient.convertToUserJson(response);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testUsername, result.username());
        assertEquals(testFirstname, result.firstname());
        assertEquals(testLastname, result.lastname());
        assertEquals(testAvatar, result.avatar());
    }
}