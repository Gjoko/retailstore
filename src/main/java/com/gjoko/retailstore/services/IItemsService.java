package com.gjoko.retailstore.services;

import com.gjoko.retailstore.persistence.dao.ItemDto;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

public interface IItemsService {

    ItemDto save(ItemDto item) throws ParseException;
    Optional<ItemDto> findById(String id);
}
