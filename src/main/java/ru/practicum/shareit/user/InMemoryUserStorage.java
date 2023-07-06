package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    public Map<Long, User> users;
    private Long id = 0L;

    public InMemoryUserStorage() {
        users = new HashMap<>();
    }


    @Override
    public User createUser(User user) {
        if (users.values().stream().noneMatch(u -> u.getEmail().equals(user.getEmail()))) {
            if (ifUserValid(user)) {
                if (user.getId() == null) {
                    user.setId(++id);
                }
                users.put(user.getId(), user);
            }
        } else {
            throw new UserAlreadyExistsException("Пользователь с почтой =" + user.getEmail() + " уже существует");
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        validation(user);
        if (users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .allMatch(u -> u.getId().equals(user.getId()))) {
            if (ifUserValid(user)) {
                users.put(user.getId(), user);
            }
        } else {
            throw new UserAlreadyExistsException("Пользователь с почтой = " + user.getEmail() + " уже существует");
        }
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("Пользователь с id = " + userId + " не найден!");
        }
        return users.get(userId);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User deleteUser(Long userId) {
        if (userId == null) {
            throw new ValidationException("id не может быть пустым");
        }
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("Пользователь с id =" + userId + " не найден!");
        }
        return users.remove(userId);
    }

    private boolean ifUserValid(User user) {
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Некорректная почта пользователя: " + user.getEmail());
        }
        if ((user.getName().isEmpty()) || (user.getName().contains(" "))) {
            throw new ValidationException("Некорректный логин пользователя: " + user.getName());
        }
        return true;
    }

    private void validation(User user) {
        if (user.getId() == null) {
            throw new ValidationException("User не найден");
        }
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с id =" + user.getId() + " не найден!");
        }
        if (user.getName() == null) {
            user.setName(users.get(user.getId()).getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(users.get(user.getId()).getEmail());
        }
    }
}
