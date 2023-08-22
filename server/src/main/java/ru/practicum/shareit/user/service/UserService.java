package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserEmailNotUniqueException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(User user) {
        User storageUser;
        try {
            storageUser = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserEmailNotUniqueException("E-mail не уникален");
        }
        return storageUser;
    }


    public User update(User user) {
        if (user != null && user.getId() > 0) {
            User storageUser = getById(user.getId());
            if (user.getEmail() != null && !user.getEmail().isBlank()) storageUser.setEmail(user.getEmail());
            if (user.getName() != null && !user.getName().isBlank()) storageUser.setName(user.getName());

            try {
                return userRepository.save(storageUser);
            } catch (DataIntegrityViolationException e) {
                throw new UserEmailNotUniqueException("E-mail не уникален");
            }
        }
        return null;
    }

    public User getById(long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        } else {
            return optionalUser.get();
        }
    }


    public List<User> getAll() {
        return userRepository.findAll();
    }


    public void delete(long userId) {
        userRepository.delete(getById(userId));
    }
}