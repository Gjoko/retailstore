package com.gjoko.retailstore.rest.api.impl;

import com.gjoko.retailstore.rest.api.IBillsController;
import com.gjoko.retailstore.rest.entity.Bill;
import com.gjoko.retailstore.services.IBillsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class BillsControllerImpl implements IBillsController {

    private final IBillsService iBillsService;

    public BillsControllerImpl(IBillsService iBillsService) {
        this.iBillsService = iBillsService;
    }

    public ResponseEntity<Bill> save(Bill bill) {
        Bill savedBill = iBillsService.save(bill);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBill);
    }

    public ResponseEntity<Bill> findById(String id) {
        Optional<Bill> bill = iBillsService.findById(id);
        if(bill.isPresent()) {
            return ResponseEntity.ok().body(bill.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Double> calculateDiscount(String id) {
        if(iBillsService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(iBillsService.calculateDiscount(id));
        }
    }
}
