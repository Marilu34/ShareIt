package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {
    private final UserDbStorage users;

    private final UserMapper mapper;


    public UserDto createUser(UserDto user) {
        return mapper.mapToUserDto(users.createUser(mapper.mapToUser(user)));
    }


    public UserDto getUserById(Long id) {
        return mapper.mapToUserDto(users.getUserById(id));
    }


    public UserDto getUsers() {
        return UserDto.builder()
                .users(users.getUsers().stream().map(mapper::mapToUserDto).collect(Collectors.toList()))
                .build();
    }


    public UserDto updateUser(UserDto user, Long userId) {
        return mapper.mapToUserDto(users.updateUser(mapper.mapToUser(user), userId));
    }


    public void deleteUser(Long id) {
        users.deleteUser(id);
    }
}
