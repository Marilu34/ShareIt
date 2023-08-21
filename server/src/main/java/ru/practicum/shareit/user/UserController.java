package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody User user) {
        log.info("Create user: " + user.toString());

        return UserDtoMapper.toUserDto(userService.create(user));
    }


    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        userDto.setId(userId);
        log.info("Update user {}: " + userDto, userId);

        User user = UserDtoMapper.toUser(userDto);

        return UserDtoMapper.toUserDto(userService.update(user));
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAll() {
        log.info("Get all users");
        return UserDtoMapper.toUserDtoList(userService.getAll());
    }


    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto get(@PathVariable Long userId) {
        log.info("Get userId {}", userId);
        return UserDtoMapper.toUserDto(userService.getById(userId));
    }


    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable int userId) {
        log.info("Delete userId {}", userId);
        userService.delete(userId);
    }
}