package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto user) throws ConflictException, ValidationException;

    UserDto update(long userId, UserDto userDto) throws NotFoundException;

    UserDto find(long id) throws NotFoundException;

    List<UserDto> findAll();

    void delete(long userId);

}
