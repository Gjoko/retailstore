package com.gjoko.retailstore.services.impl;

import com.gjoko.retailstore.persistence.dao.UserDto;
import com.gjoko.retailstore.persistence.repositories.UsersRepository;
import com.gjoko.retailstore.services.IUsersService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersServiceImpl implements IUsersService {

    private final UsersRepository usersRepository;

    public UsersServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public Optional<UserDto> getByUsername(String username) {
        return Optional.ofNullable(usersRepository.findByUsername(username));
    }

    @Override
    public Optional<UserDto> getById(String id) {
        return usersRepository.findById(id);
    }

    @Override
    public UserDto save(UserDto userDto) {
        return usersRepository.save(userDto);
    }
}
