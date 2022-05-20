package com.gjoko.retailstore.services;

import com.gjoko.retailstore.persistence.dao.UserDto;

import java.util.Optional;

public interface IUsersService {

    Optional<UserDto> getByUsername(String username);
    Optional<UserDto> getById(String id);
    UserDto save(UserDto userDto);
}
