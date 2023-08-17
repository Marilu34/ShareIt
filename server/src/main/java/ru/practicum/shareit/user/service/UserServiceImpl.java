package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Validator validator;


    public UserDto createUser(UserDto userDto) {
        validate(userDto);
        User user = userRepository.save(UserMapper.fromUserDto(userDto));
        return UserMapper.toUserDto(user);
    }


    private void validate(UserDto userDto) {
        List<String> mistakes = new ArrayList<>();

        validator.validate(userDto).forEach(mistake -> {
            String message = mistake.getPropertyPath() + ": " + mistake.getMessage();
            mistakes.add(message);
        });

        if (!mistakes.isEmpty()) {
            throw new ValidationException("Ошибки: " + mistakes);
        }
    }


    @Transactional(readOnly = true)
    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("объект Пользователь не найден в репозитории"));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public UserDto updateUser(UserDto userDto) {
        UserDto userUpdate = getUserById(userDto.getId());

        if (userDto.getName() != null) {
            userUpdate.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userUpdate.setEmail(userDto.getEmail());
        }
        validate(userUpdate);

        User user = userRepository.save(UserMapper.fromUserDto(userUpdate));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("объект Пользователь не найден");
        }
        userRepository.deleteById(userId);
    }

    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
