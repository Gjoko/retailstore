package com.gjoko.retailstore.rest.api;


import com.gjoko.retailstore.rest.entity.Item;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RequestMapping("/api/items")
public interface IItemController {

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @PostMapping
    ResponseEntity<Item> save(@RequestBody Item item) throws ParseException;

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/{id}")
    ResponseEntity<Item> findById(@PathVariable(value = "id") String id);
}
