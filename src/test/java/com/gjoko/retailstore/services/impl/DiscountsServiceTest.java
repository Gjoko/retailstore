package com.gjoko.retailstore.services.impl;

import com.gjoko.retailstore.persistence.dao.BillDto;
import com.gjoko.retailstore.persistence.dao.ItemDto;
import com.gjoko.retailstore.persistence.dao.UserDto;
import com.gjoko.retailstore.persistence.repositories.ItemsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gjoko.retailstore.helper.TestHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscountsServiceTest {

  @InjectMocks private DiscountsService discountsService;

  @Mock private ItemsRepository itemsRepository;

  private List<ItemDto> items;

  private BillDto billDto;

  private UserDto userDto;

  @BeforeEach
  private void setUp() {
    items = new ArrayList<>();
    ItemDto bbq = createBBQItem();
    bbq.setId("1");
    ItemDto cucumber = createCucumberItem();
    cucumber.setId("2");
    items.add(bbq);
    items.add(cucumber);

    billDto = new BillDto();
    billDto.setId("13");
    Map<String, Integer> items = new HashMap<>();
    items.put(bbq.getId(), 1);
    items.put(cucumber.getId(), 5);
    billDto.setItems(items);

    userDto = createUserRoleEmployee();
  }

  @Test
  public void givenEmployeeCreatesBill_whenThereIsDiscount_thenCalculateDiscount() {
    assertNotNull(itemsRepository);
    when(itemsRepository.findByIds(any(), any())).thenReturn(items);
    DiscountsService discountsService = new DiscountsService(itemsRepository);
    assertEquals(410d, discountsService.applyDiscountsToBill(billDto, userDto));
  }
}
