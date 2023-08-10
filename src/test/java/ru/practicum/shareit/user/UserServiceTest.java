package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void updateUserFields_shouldUpdateOnlyName_whenPassedOnlyName() {
        long userId = 2;
        String newName = "new name";
        User user = User.builder().id(userId).email("email@email.net").name("name").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).then(AdditionalAnswers.returnsFirstArg());

        UserDto actual = service.updateUser(UserDto.builder().id(userId).name(newName).build());

        assertEquals(newName, actual.getName());
        assertEquals(user.getEmail(), actual.getEmail());
        assertEquals(user.getId(), actual.getId());

    }

    @Test
    void updateUserFields_shouldThrowConstraintViolationException_whenPassedIncorrectEmail() {
        long userId = 2;
        String wrongEmail = "incorrectEmail";
        User user = User.builder().id(userId).email("email@email.net").name("name").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class,
                () -> service.updateUser(UserDto.builder().id(userId).email(wrongEmail).build()));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void delete_shouldInvokeRepositoryDeleteMethod_whenInvokedForExistingUser() {
        long userId = 123;
        when(userRepository.existsById(userId)).thenReturn(true);

        service.deleteUser(userId);

        verify(userRepository, atLeastOnce()).deleteById(userId);
    }

    @Test
    void delete_shouldThrowUserNotFoundException_whenInvokedForNotExistingUser() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.deleteUser(anyLong()));

        verify(userRepository, Mockito.never()).deleteById(anyLong());
    }

    @Test
    void getAll_shouldReturnCollectionWithOneUser_whenRepositoryHaveOneUser() {
        User user = User.builder().id(1).email("email@email.net").name("name").build();
        when(userRepository.findAll()).thenReturn(List.of(user));

        Collection<UserDto> actual = service.getAllUsers();

        assertEquals(1, actual.size());
        assertEquals(UserMapper.toUserDto(user), actual.iterator().next());
    }
}