package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class UserService {
    private UserStorage userStorage;
    private UserMapper userMapper;

    @Autowired
    public UserService(@Qualifier("InMemoryUserStorage") UserStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }


    public UserDto createUser(UserDto userDto) {
        return userMapper.toUserDto(userStorage.createUser(userMapper.toUser(userDto)));
    }

    public UserDto updateUser(UserDto userDto, Long id) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        return userMapper.toUserDto(userStorage.updateUser(userMapper.toUser(userDto)));
    }

    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(userMapper::toUserDto)
                .collect(toList());
    }


    public UserDto getUserById(Long id) {
        return userMapper.toUserDto(userStorage.getUserById(id));
    }


    public UserDto deleteUser(Long userId) {
        return userMapper.toUserDto(userStorage.deleteUser(userId));
    }

    public boolean ifUserExist(Long userId) {
        boolean exist = false;
        if (getUserById(userId) != null) {
            exist = true;
        }
        return exist;
    }}