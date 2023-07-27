package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto user) throws ConflictException, ValidationException;

    UserDto update(long userId, UserDto userDto) ;

    UserDto find(long id);

    List<UserDto> findAll();

    void delete(long userId);

}
