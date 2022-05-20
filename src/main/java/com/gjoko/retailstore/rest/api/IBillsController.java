package com.gjoko.retailstore.rest.api;

import com.gjoko.retailstore.rest.entity.Bill;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/bills")
public interface IBillsController {

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @PostMapping
    ResponseEntity<Bill> save(@RequestBody Bill bill);

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/{id}")
    ResponseEntity<Bill> findById(@PathVariable(value = "id") String id);

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/calculateDiscount/{id}")
    ResponseEntity<Double> calculateDiscount(@PathVariable(value = "id") String id);
}
