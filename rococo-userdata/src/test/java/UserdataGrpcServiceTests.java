import anbrain.qa.rococo.data.UserProfileEntity;
import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.service.UserdataDatabaseService;
import anbrain.qa.rococo.service.UserdataGrpcService;
import io.grpc.stub.StreamObserver;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserdataGrpcServiceTests {

    @Mock
    private UserdataDatabaseService userdataDatabaseService;

    @Mock
    private StreamObserver<UserResponse> responseObserver;

    @InjectMocks
    private UserdataGrpcService userdataGrpcService;

    private final String testUsername = "testuser";
    private UserProfileEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserProfileEntity();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername(testUsername);
        testUser.setFirstname("Test");
        testUser.setLastname("User");
        testUser.setAvatar("avatar".getBytes());
    }

    @Test
    void shouldSuccessfullyGetUser() {
        UserRequest request = UserRequest.newBuilder()
                .setUsername(testUsername)
                .build();

        when(userdataDatabaseService.getUserByUsername(testUsername)).thenReturn(testUser);

        userdataGrpcService.getUser(request, responseObserver);

        ArgumentCaptor<UserResponse> captor = ArgumentCaptor.forClass(UserResponse.class);
        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        UserResponse response = captor.getValue();
        assertEquals(testUser.getId().toString(), response.getId());
        assertEquals(testUsername, response.getUsername());
        assertEquals("Test", response.getFirstname());
        assertEquals("User", response.getLastname());
        assertEquals("avatar", response.getAvatar());
    }

    @Test
    void shouldHandleErrorWhenUserNotFound() {
        UserRequest request = UserRequest.newBuilder()
                .setUsername(testUsername)
                .build();

        when(userdataDatabaseService.getUserByUsername(testUsername))
                .thenThrow(new EntityNotFoundException("Пользователь не найден"));

        assertThrows(
                EntityNotFoundException.class,
                () -> userdataGrpcService.getUser(request, responseObserver)
        );

        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();
    }

    @Test
    void shouldSuccessfullyUpdateUser() {
        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setUsername(testUsername)
                .setFirstname("NewFirst")
                .setLastname("NewLast")
                .setAvatar("newAvatar")
                .build();

        UserProfileEntity updatedUser = new UserProfileEntity();
        updatedUser.setId(testUser.getId());
        updatedUser.setUsername(testUsername);
        updatedUser.setFirstname("NewFirst");
        updatedUser.setLastname("NewLast");
        updatedUser.setAvatar("newAvatar".getBytes());

        when(userdataDatabaseService.updateUser(
                eq(testUsername),
                eq("NewFirst"),
                eq("NewLast"),
                eq("newAvatar")
        )).thenReturn(updatedUser);

        userdataGrpcService.updateUser(request, responseObserver);

        ArgumentCaptor<UserResponse> captor = ArgumentCaptor.forClass(UserResponse.class);
        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        UserResponse response = captor.getValue();
        assertEquals(testUser.getId().toString(), response.getId());
        assertEquals("NewFirst", response.getFirstname());
        assertEquals("NewLast", response.getLastname());
        assertEquals("newAvatar", response.getAvatar());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithBlankFirstname() {
        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setUsername(testUsername)
                .setFirstname(" ")
                .setLastname("ValidLastname")
                .setAvatar("avatar")
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userdataGrpcService.updateUser(request, responseObserver)
        );

        assertEquals("Имя и фамилия обязательны для заполнения", exception.getMessage());
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithBlankLastname() {
        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setUsername(testUsername)
                .setFirstname("ValidFirstname")
                .setLastname(" ")
                .setAvatar("avatar")
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userdataGrpcService.updateUser(request, responseObserver)
        );

        assertEquals("Имя и фамилия обязательны для заполнения", exception.getMessage());
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();
    }

    @Test
    void shouldUpdateUserWithoutAvatarWhenAvatarIsBlank() {
        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setUsername(testUsername)
                .setFirstname("First")
                .setLastname("Last")
                .setAvatar("")
                .build();

        UserProfileEntity updatedUser = new UserProfileEntity();
        updatedUser.setId(testUser.getId());
        updatedUser.setUsername(testUsername);
        updatedUser.setFirstname("First");
        updatedUser.setLastname("Last");
        updatedUser.setAvatar(null);

        when(userdataDatabaseService.updateUser(
                eq(testUsername),
                eq("First"),
                eq("Last"),
                eq("")
        )).thenReturn(updatedUser);

        userdataGrpcService.updateUser(request, responseObserver);

        ArgumentCaptor<UserResponse> captor = ArgumentCaptor.forClass(UserResponse.class);
        verify(responseObserver).onNext(captor.capture());

        UserResponse response = captor.getValue();
        assertEquals("", response.getAvatar());
    }

    @Test
    void shouldConvertEntityToGrpcResponseCorrectly() {
        testUser.setFirstname(null);
        testUser.setLastname(null);
        testUser.setAvatar(null);

        UserResponse response = userdataGrpcService.convertEntityToGrpcResponse(testUser);

        assertEquals("", response.getFirstname());
        assertEquals("", response.getLastname());
        assertEquals("", response.getAvatar());
    }
}