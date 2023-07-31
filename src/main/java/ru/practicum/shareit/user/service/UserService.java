package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto get(long userId);

    UserDto updateUserFields(UserDto userDto);

    void delete(Long userId);

    Collection<UserDto> getAll();

}
