package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Spy
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testGetAll() {
        User user = User.builder().id(1).email("email@yandex.ru").name("name").build();
        when(userRepository.findAll()).thenReturn(List.of(user));
        Collection<UserDto> actual = userService.getAllUsers();
        assertEquals(1, actual.size());
        assertEquals(UserMapper.toUserDto(user), actual.iterator().next());
    }


    @Test
    void testUpdate() {
        User user = User.builder().id(1L).email("email@yandex.ru").name("name").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).then(AdditionalAnswers.returnsFirstArg());
        UserDto user1 = userService.updateUser(UserDto.builder().id(1L).email("email@yandex.ru").build());
        assertEquals(user.getEmail(), user1.getEmail());
        assertEquals(user.getId(), user1.getId());
    }

    @Test
    void testWrongUpdate() {
        long userId = 2;
        String wrongEmail = "wrongEmail";
        User user = User.builder().id(userId).email("email@yandex.ru").name("name").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class,
                () -> userService.updateUser(UserDto.builder().id(userId).email(wrongEmail).build()));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testDelete() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.deleteUser(1L);
        verify(userRepository, atLeastOnce()).deleteById(1L);
    }


    @Test
    void testShouldReturnMistakeIfUserIsNotExist() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> userService.deleteUser(anyLong()));
        verify(userRepository, Mockito.never()).deleteById(anyLong());
    }
}