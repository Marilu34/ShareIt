package ru.practicum.shareit.user;

import java.util.List;

public interface UserDbStorage {
    User createUser(User user);

    User getUserById(Long id);

    List<User> getUsers();

    User updateUser(User user, Long userId);

    void deleteUser(Long id);
}