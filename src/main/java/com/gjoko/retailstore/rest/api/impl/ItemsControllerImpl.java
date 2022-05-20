package com.gjoko.retailstore.rest.api.impl;

import com.gjoko.retailstore.persistence.dao.ItemDto;
import com.gjoko.retailstore.rest.api.IItemController;
import com.gjoko.retailstore.rest.entity.Item;
import com.gjoko.retailstore.services.IItemsService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Optional;

import static com.gjoko.retailstore.util.CurrencyUtils.doubleToString;
import static com.gjoko.retailstore.util.CurrencyUtils.stringToDouble;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
public class ItemsControllerImpl implements IItemController {

  private final IItemsService itemsService;
  private final ModelMapper modelMapper;

  public ItemsControllerImpl(IItemsService itemsService, ModelMapper modelMapper) {
    this.itemsService = itemsService;
    this.modelMapper = modelMapper;
  }

  public ResponseEntity<Item> save(Item item) throws ParseException {
    ItemDto itemDto = modelMapper.map(item, ItemDto.class);
    itemDto.setAmount(stringToDouble(item.getPrice()));
    itemDto = itemsService.save(itemDto);
    item.setId(itemDto.getId());
    return ResponseEntity.status(CREATED).body(item);
  }

  public ResponseEntity<Item> findById(String id) {
    Optional<ItemDto> itemDtoOptional = itemsService.findById(id);
    if (itemDtoOptional.isPresent()) {
      Item item = modelMapper.map(itemDtoOptional.get(), Item.class);
      item.setPrice(doubleToString(itemDtoOptional.get().getAmount()));
      return ResponseEntity.ok().body(item);
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
