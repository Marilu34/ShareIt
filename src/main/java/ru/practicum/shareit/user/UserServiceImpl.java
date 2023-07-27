package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service

public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    public UserDto create(UserDto userDto) throws ConflictException, ValidationException {
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
    public UserDto update(long userId, UserDto userDto) throws NotFoundException {
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
    public UserDto find(long id) throws NotFoundException {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("");
        }
        return UserMapper.toUserDto(user.get());
    }

    @Override
    public List<UserDto> findAll() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long userId) {
        repository.delete(repository.getReferenceById(userId));
    }


}