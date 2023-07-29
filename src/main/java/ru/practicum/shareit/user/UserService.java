package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto user) throws ConflictException, ValidationException;

    UserDto updateUser(Long userId, UserDto userDto) throws NotFoundException;

    UserDto getUser(Long id) throws NotFoundException;

    List<UserDto> getAllUsers();

    void deleteUser(Long userId);

}
