package com.gjoko.retailstore.services.impl;

import com.gjoko.retailstore.persistence.dao.ItemDto;
import com.gjoko.retailstore.persistence.repositories.ItemsRepository;
import com.gjoko.retailstore.services.IItemsService;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@Service
public class ItemsServiceImpl implements IItemsService {

  private final ItemsRepository itemsRepository;

  public ItemsServiceImpl(ItemsRepository itemsRepository) {
    this.itemsRepository = itemsRepository;
  }

  @Override
  public ItemDto save(ItemDto item) throws ParseException {
    return itemsRepository.save(item);
  }

  @Override
  public Optional<ItemDto> findById(String id) {
    return itemsRepository.findById(id);
  }
}
