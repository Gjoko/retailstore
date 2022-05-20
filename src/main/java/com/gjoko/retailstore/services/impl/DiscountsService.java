package com.gjoko.retailstore.services.impl;

import com.gjoko.retailstore.persistence.dao.BillDto;
import com.gjoko.retailstore.persistence.dao.ItemDto;
import com.gjoko.retailstore.persistence.dao.UserDto;
import com.gjoko.retailstore.persistence.repositories.ItemsRepository;
import com.gjoko.retailstore.util.DiscountUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiscountsService {

    private final ItemsRepository itemsRepository;

    public DiscountsService(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    public Double applyDiscountsToBill(BillDto billDto, UserDto userDto) {
        Double priceAfterDiscounts = 0d;
        List<String> itemIdList = new ArrayList<>(billDto.getItems().keySet());
        List<ItemDto> itemsList = itemsRepository.findByIds(itemIdList, Sort.by("_id"));

        Double nonGroceriesTotalPrice = DiscountUtils.calculateTotalPriceOnNonGroceries(billDto, itemsList);
        Double groceriesTotalPrice = DiscountUtils.calculateTotalPriceOnGroceries(billDto, itemsList);
        Double discountedNonGroceriesTotalPrice = DiscountUtils.applyPercentageDiscountOnNonGroceries(nonGroceriesTotalPrice, userDto);
        priceAfterDiscounts = DiscountUtils.apply5DollarDiscount(discountedNonGroceriesTotalPrice + groceriesTotalPrice);

        return priceAfterDiscounts;
    }
}
