package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

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


    public List<UserDto> getUsers() {
        List<User> users = userStorage.getUsers();
        List<UserDto> userDtos = new ArrayList<>();

        for (User user : users) {
            UserDto userDto = userMapper.toUserDto(user);
            userDtos.add(userDto);
        }

        return userDtos;
    }


    public UserDto updateUser(UserDto userDto, Long id) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        return userMapper.toUserDto(userStorage.updateUser(userMapper.toUser(userDto)));
    }

    public UserDto getUserById(Long userId) {
        return userMapper.toUserDto(userStorage.getUserById(userId));
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
    }
}