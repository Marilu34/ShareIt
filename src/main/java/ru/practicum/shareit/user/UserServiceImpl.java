package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.UserNotFoundException;
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
    public UserDto update(long userId, UserDto userDto) throws UserNotFoundException {
        User user = repository.getReferenceById(userId);
        User newUser = UserMapper.fromUserDto(userDto);
        if (user != null) {
            if (UserValidator.isName(newUser.getName())) {
                user.setName(newUser.getName());
            }
            if (UserValidator.isEmail(newUser.getEmail())) {
                user.setEmail(newUser.getEmail());
            }
        } else throw new UserNotFoundException("Пользователь не найден");
        user = repository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto find(long id) throws UserNotFoundException {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("");
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

//    private void validation(User user) {
//        if ((user.getId() == null) || (user.getId() <= 0)) {
//            throw new ValidationException("User не найден");
//        }
////        if ((user.getName() == null) || user.getName().isEmpty()) {
////            user.setName(user.getEmail());
////        }
//        if ((user.getName() == null) || (user.getName().isEmpty()) || (user.getEmail() == null) && (user.getEmail().isEmpty()) &&
//                Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$", user.getEmail())) {
//            throw new ValidationException("Некорректный запрос при добавлении пользователя");
//        }
//    }
}