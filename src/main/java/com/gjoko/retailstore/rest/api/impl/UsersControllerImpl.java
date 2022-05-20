package com.gjoko.retailstore.rest.api.impl;

import com.gjoko.retailstore.persistence.dao.UserDto;
import com.gjoko.retailstore.rest.api.IUsersController;
import com.gjoko.retailstore.rest.entity.User;
import com.gjoko.retailstore.services.IUsersService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UsersControllerImpl implements IUsersController {

  private final IUsersService usersService;

  private final ModelMapper modelMapper;

  public UsersControllerImpl(IUsersService usersService, ModelMapper modelMapper) {
    this.usersService = usersService;
    this.modelMapper = modelMapper;
  }

  @Override
  public ResponseEntity<User> getById(String id) {
    return createCorrespondingResponse(usersService.getById(id));
  }

  @Override
  public ResponseEntity<User> getByUsername(String username) {
    return createCorrespondingResponse(usersService.getByUsername(username));
  }

  @Override
  public ResponseEntity<User> create(User user) {
    UserDto userDto = modelMapper.map(user, UserDto.class);
    userDto = usersService.save(userDto);
    user.setId(userDto.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  private ResponseEntity<User> createCorrespondingResponse(Optional<UserDto> optionalUserDto) {
    if (optionalUserDto.isPresent()) {
      UserDto userDto = optionalUserDto.get();
      User user = modelMapper.map(userDto, User.class);
      return ResponseEntity.ok(user);
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
