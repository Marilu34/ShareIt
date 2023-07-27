package ru.practicum.shareit.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @SneakyThrows
    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос на добавление пользователя");
        return service.create(userDto);
    }

    @SneakyThrows
    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @Valid @RequestBody UserDto user) {
        log.info("Получен запрос на изменение данных пользователя");
        return service.update(userId, user);
    }


    @SneakyThrows
    @GetMapping("/{id}")
    public UserDto find(@PathVariable long id) {
        log.info("Получен запрос на вывод данных пользователя");
        return service.find(id);
    }

    @SneakyThrows
    @GetMapping
    public List<UserDto> findAll() {
        return service.findAll();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Получен запрос на удаление пользователя");
        service.delete(userId);
    }
}