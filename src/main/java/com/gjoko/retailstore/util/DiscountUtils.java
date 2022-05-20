package com.gjoko.retailstore.util;

import com.gjoko.retailstore.persistence.dao.BillDto;
import com.gjoko.retailstore.persistence.dao.ItemDto;
import com.gjoko.retailstore.persistence.dao.UserDto;
import com.gjoko.retailstore.persistence.enums.ItemType;

import java.time.LocalDateTime;
import java.util.List;

public class DiscountUtils {

  public static Double calculateTotalPriceOnNonGroceries(BillDto billDto, List<ItemDto> itemsList) {
    Double sumOfNonGroceries = 0d;
    sumOfNonGroceries =
        itemsList.stream()
            .filter(itemDto -> !itemDto.getType().equals(ItemType.GROCERIES))
            .map(
                itemDto ->
                    itemDto.getAmount().doubleValue() * billDto.getItems().get(itemDto.getId()))
            .mapToDouble(Double::doubleValue)
            .sum();

    return sumOfNonGroceries;
  }

  public static Double calculateTotalPriceOnGroceries(BillDto billDto, List<ItemDto> itemsList) {
    Double sumOfGroceries = 0d;
    sumOfGroceries =
        itemsList.stream()
            .filter(itemDto -> itemDto.getType().equals(ItemType.GROCERIES))
            .map(
                itemDto ->
                    itemDto.getAmount().doubleValue() * billDto.getItems().get(itemDto.getId()))
            .mapToDouble(Double::doubleValue)
            .sum();

    return sumOfGroceries;
  }

  public static Double applyPercentageDiscountOnNonGroceries(
      Double nonGrouceriesTotalPrice, UserDto userDto) {
    Double nonGroceriesDiscountedPrice = 0d;
    if (nonGrouceriesTotalPrice != 0L) {
      switch (userDto.getRole()) {
        case ROLE_EMPLOYEE:
          nonGroceriesDiscountedPrice = nonGrouceriesTotalPrice.doubleValue() * 0.3d;
          break;
        case ROLE_AFFILIATE:
          nonGroceriesDiscountedPrice = nonGrouceriesTotalPrice.doubleValue() * 0.1d;
          break;
        case ROLE_CUSTOMER:
          if (userDto.getCreated().plusYears(2).isBefore(LocalDateTime.now())) {
            nonGroceriesDiscountedPrice = nonGrouceriesTotalPrice.doubleValue() * 0.05d;
          }
      }
    }
    return nonGrouceriesTotalPrice - nonGroceriesDiscountedPrice;
  }

  public static Double apply5DollarDiscount(Double amount) {
    return amount - ((int) (amount / 100)) * 5;
  }
}
