package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(userDto));
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") @Min(1) Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
    }

    @GetMapping
    public UserDto getAllUsers() {
        return userService.getUsers();
    }

    @PatchMapping("{id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable("id") Long userId) {
        return userService.updateUser(userDto, userId);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@Min(1) @PathVariable("id") Long userId) {
        userService.deleteUser(userId);
    }

}
