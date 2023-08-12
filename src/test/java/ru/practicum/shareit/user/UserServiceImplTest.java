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

    @Test
    void testGetUserById() {
        long userId = 4L; // Замените на существующий идентификатор пользователя

        // Создаем тестового пользователя
        User user = new User();
        user.setId(userId);
        user.setName("John");

        // Настраиваем поведение мока
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Получаем результат вызова метода getUserById
        UserDto result = userService.getUserById(userId);

        // Проверяем, что метод findById был вызван с правильным аргументом
        Mockito.verify(userRepository).findById(userId);

        // Проверяем, что результат не равен null
        assertNotNull(result);

        // Проверяем правильность преобразования из User в UserDto
        assertEquals(userId, result.getId());
        assertEquals("John", result.getName());
    }

    @Test
    void testCreateUser() {
        // Создаем тестовый UserDto
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("john1@mail.ru");
        user1.setName("John1");

        // Создаем тестового пользователя
        User user = new User();
        user.setId(1L);
        user.setEmail("john@mail.ru");
        user.setName("John");

        // Настраиваем поведение мока
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // Вызываем метод createUser
        UserDto result = userService.createUser(UserMapper.toUserDto(user1));

        // Проверяем, что метод save был вызван с правильным аргументом
        Mockito.verify(userRepository).save(Mockito.any(User.class));

        // Проверяем, что результат не равен null
        assertNotNull(result);

        // Проверяем правильность преобразования из User в UserDto
        assertEquals(result.getId(), 1L);
        assertEquals(result.getName(), "John");
    }
}

