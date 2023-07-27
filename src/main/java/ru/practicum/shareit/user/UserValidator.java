package ru.practicum.shareit.user;

import java.util.regex.Pattern;

public class UserValidator {

    public static boolean isName(String name) {
        return (name != null && !name.isEmpty());
    }

    public static boolean isEmail(String email) {
        return (email != null && !email.isEmpty() &&
                Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$",
                        email));
    }

}
