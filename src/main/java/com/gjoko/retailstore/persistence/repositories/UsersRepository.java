package com.gjoko.retailstore.persistence.repositories;

import com.gjoko.retailstore.persistence.dao.UserDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UsersRepository extends MongoRepository<UserDto, String> {

    @Query("{username:'?0'}")
    UserDto findByUsername(String username);
}
