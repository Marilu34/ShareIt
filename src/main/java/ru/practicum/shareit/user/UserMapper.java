package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class  UserMapper {


    public UserDto mapToUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.id( user.getId() );
        userDto.name( user.getName() );
        userDto.email( user.getEmail() );

        return userDto.build();
    }


    public User mapToUser(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( userDto.getId() );
        user.name( userDto.getName() );
        user.email( userDto.getEmail() );

        return user.build();
    }
}
