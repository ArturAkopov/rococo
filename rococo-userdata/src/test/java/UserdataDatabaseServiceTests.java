import anbrain.qa.rococo.data.UserProfileEntity;
import anbrain.qa.rococo.data.repository.UserProfileRepository;
import anbrain.qa.rococo.model.UserJson;
import anbrain.qa.rococo.service.UserdataDatabaseService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserdataDatabaseServiceTests {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserdataDatabaseService userdataDatabaseService;

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
    void shouldSuccessfullyGetUserByUsername() {
        when(userProfileRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        UserProfileEntity result = userdataDatabaseService.getUserByUsername(testUsername);

        assertNotNull(result);
        assertEquals(testUsername, result.getUsername());
        verify(userProfileRepository).findByUsername(testUsername);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userProfileRepository.findByUsername(testUsername)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userdataDatabaseService.getUserByUsername(testUsername)
        );

        assertEquals("Пользователь не найден: " + testUsername, exception.getMessage());
    }

    @Test
    void shouldSuccessfullyUpdateUser() {
        String newFirstname = "NewFirst";
        String newLastname = "NewLast";
        String newAvatar = "newAvatar";

        when(userProfileRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(userProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileEntity result = userdataDatabaseService.updateUser(testUsername, newFirstname, newLastname, newAvatar);

        assertNotNull(result);
        assertEquals(newFirstname, result.getFirstname());
        assertEquals(newLastname, result.getLastname());
        assertArrayEquals(newAvatar.getBytes(), result.getAvatar());
    }

    @Test
    void shouldUpdateUserWithAvatarWhenAvatarIsBlank() {
        String newFirstname = "NewFirst";
        String newLastname = "NewLast";

        when(userProfileRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(userProfileRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UserProfileEntity result = userdataDatabaseService.updateUser(testUsername, newFirstname, newLastname, "");

        assertNotNull(result);
        assertEquals(newFirstname, result.getFirstname());
        assertEquals(newLastname, result.getLastname());
        assertEquals(testUser.getAvatar(),result.getAvatar());
    }

    @Test
    void shouldCreateNewUserFromKafkaWhenNotExists() {
        when(userProfileRepository.findByUsername(testUsername)).thenReturn(Optional.empty());
        when(userProfileRepository.save(any())).thenReturn(testUser);

        final UserJson userJson = new UserJson(
                null,
                testUsername,
                null,
                null
                ,null
        );

        userdataDatabaseService.createUser(userJson);

        ArgumentCaptor<UserProfileEntity> captor = ArgumentCaptor.forClass(UserProfileEntity.class);
        verify(userProfileRepository).save(captor.capture());

        UserProfileEntity saved = captor.getValue();
        assertEquals(testUsername, saved.getUsername());
    }

    @Test
    void shouldNotCreateUserFromKafkaWhenAlreadyExists() {
        when(userProfileRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        final UserJson userJson = new UserJson(
                null,
                testUsername,
                null,
                null
                ,null
        );
        userdataDatabaseService.createUser(userJson);

        verify(userProfileRepository, never()).save(any());
    }
}