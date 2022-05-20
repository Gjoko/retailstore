package com.gjoko.retailstore.services;

import com.gjoko.retailstore.rest.entity.Bill;

import java.util.Optional;

public interface IBillsService {

    Bill save(Bill bill);
    Optional<Bill> findById(String id);
    Double calculateDiscount(String id);
}
