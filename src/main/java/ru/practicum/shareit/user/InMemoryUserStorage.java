package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.InMemoryItemStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    public HashMap<Long, User> users;
    private Long userId = 0L;
    InMemoryItemStorage itemStorage;

    public InMemoryUserStorage() {
        itemStorage =  new InMemoryItemStorage();
        users = new HashMap<>();
    }


    @Override
    public User createUser(User user) {
        boolean emailExists = false;
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail())) {
                emailExists = true;
                break;
            }
        }
        if (!emailExists) {
            if (ifUserValid(user)) {
                if (user.getId() == null) {
                    user.setId(++userId);
                }
                users.put(user.getId(), user);
            }
        } else {
            throw new UserAlreadyExistsException("Пользователь с почтой = " + user.getEmail() + " уже существует");
        }
        return user;
    }


    @Override
    public User updateUser(User user) {
        validation(user);
        boolean emailExists = false;
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail())) {
                if (!u.getId().equals(user.getId())) {
                    emailExists = true;
                    break;
                }
            }
        }
        if (!emailExists) {
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
        itemStorage.deleteItemsByOwner(userId);
        return users.remove(userId);
    }

    private boolean ifUserValid(User user) {
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Некорректная почта пользователя: " + user.getEmail());
        }
        if ((user.getName().isEmpty()) || (user.getName().contains(" "))) {
            throw new ValidationException("логин не может быть пустым или содержать пробелы");
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
