package com.gjoko.retailstore.rest.api;

import com.gjoko.retailstore.rest.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/users")
public interface IUsersController {

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/{id}")
    ResponseEntity<User> getById(@PathVariable(value = "id") String id);

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping
    ResponseEntity<User> getByUsername(@RequestParam(name = "username") String username);

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    ResponseEntity<User> create(@RequestBody User user);
}
