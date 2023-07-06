package ru.practicum.shareit.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j

public class UserDbStorageImpl implements UserDbStorage {
    private final Map<Long, User> users;
    private Long id= 1L;//не можеT начинаться с нуля;

    public UserDbStorageImpl() {
        this.users = new HashMap<>();

    }

    @Override
    public User createUser(User user) {
        validateUser(user);
        user.setId(generatedId());
        users.put(user.getId(), user);
        log.info("Пользователь {} добавлен", user);
        return user;
    }

    @Override
    public User getUserById(Long id) throws ResponseStatusException {
        if (!users.containsKey(id)) {
            log.warn("Пользователя с id={} нет", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + id + " нет");
        }
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(User user, Long userId) {
        if (users.get(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + userId + " нет");
        }
        validateUser(user);
        user.setId(userId);
        updateUserNameAndEmail(user);
        users.put(userId, user);
        log.info("Пользовалель с id={} обновлен", user.getId());
        return users.get(user.getId());
    }

    @Override
    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            log.warn("Пользователя с id={} нет", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + id + " нет");
        }
        users.remove(id);
        log.info("Пользовалель с id={} удален", id);
    }

    private Long generatedId() {
        return id++;
    }

    private void validateUser(User user) {
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            log.warn("email {} уже занят", user.getEmail());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "email " + user.getEmail() + " уже занят");
        }
    }

    private void updateUserNameAndEmail(User user) {
        if (user.getName() != null) {
            users.get(user.getId()).setName(user.getName());
        } else {
            user.setName(users.get(user.getId()).getName());
        }
        if (user.getEmail() != null) {
            users.get(user.getId()).setName(user.getEmail());
        } else {
            user.setEmail(users.get(user.getId()).getEmail());
        }
    }
}
