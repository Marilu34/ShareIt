package ru.practicum.shareit.user;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    @Autowired
    private final UserService service;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) throws ConflictException {
        log.info("Получен запрос на добавление пользователя");
        return service.createUser(userDto);
    }


    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @Valid @RequestBody UserDto user) {
        log.info("Получен запрос на изменение данных пользователя");
        return service.updateUser(userId, user);
    }


    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        log.info("Получен запрос на вывод данных пользователя");
        return service.getUser(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя");
        service.deleteUser(userId);
    }
}