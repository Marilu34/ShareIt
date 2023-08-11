package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserById(long userId);

    UserDto updateUser(UserDto userDto);

    void deleteUser(Long userId);

    Collection<UserDto> getAllUsers();

}
