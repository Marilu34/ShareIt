package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository repository;


    public UserDto createUser(UserDto userDto) throws ConflictException, ValidationException {
        User user = UserMapper.fromUserDto(userDto);
        if (!UserValidator.isName(user.getName()) || !UserValidator.isEmail(user.getEmail())) {
            throw new ValidationException("Некорректный запрос при добавлении пользователя");
        }
        user = repository.save(user);
        if (user == null) {
            throw new ConflictException("Конфликт, пользователь с таким Email существует");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) throws NotFoundException {
        User user = repository.getReferenceById(userId);
        User newUser = UserMapper.fromUserDto(userDto);
        if (user != null) {
            if (UserValidator.isName(newUser.getName())) {
                user.setName(newUser.getName());
            }
            if (UserValidator.isEmail(newUser.getEmail())) {
                user.setEmail(newUser.getEmail());
            }
        } else throw new NotFoundException("Пользователь не найден");
        user = repository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUser(Long id) throws NotFoundException {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("");
        }
        return UserMapper.toUserDto(user.get());
    }

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        repository.delete(repository.getReferenceById(userId));
    }


}