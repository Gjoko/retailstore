package com.gjoko.retailstore.services.impl;

import com.gjoko.retailstore.persistence.dao.BillDto;
import com.gjoko.retailstore.persistence.dao.UserDto;
import com.gjoko.retailstore.persistence.repositories.BillsRepository;
import com.gjoko.retailstore.persistence.repositories.UsersRepository;
import com.gjoko.retailstore.rest.entity.Bill;
import com.gjoko.retailstore.services.IBillsService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BillsServiceImpl implements IBillsService {

  private final BillsRepository billsRepository;
  private final DiscountsService discountsService;
  private final ModelMapper modelMapper;
  private final UsersRepository usersRepository;

  public BillsServiceImpl(
      BillsRepository billsRepository,
      DiscountsService discountsService,
      ModelMapper modelMapper,
      UsersRepository usersRepository) {
    this.billsRepository = billsRepository;
    this.discountsService = discountsService;
    this.modelMapper = modelMapper;
    this.usersRepository = usersRepository;
  }

  @Override
  public Bill save(Bill bill) {
    BillDto billDto = modelMapper.map(bill, BillDto.class);
    billDto = billsRepository.save(billDto);
    return modelMapper.map(billDto, Bill.class);
  }

  @Override
  public Optional<Bill> findById(String id) {
    BillDto billDto = billsRepository.findBillDtoById(id);
    if(billDto != null) {
      return Optional.ofNullable(modelMapper.map(billDto, Bill.class));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Double calculateDiscount(String id) {
    BillDto billDto = billsRepository.findBillDtoById(id);
    UserDto userDto = usersRepository.findById(billDto.getUserId()).get();
    return discountsService.applyDiscountsToBill(billDto, userDto);

  }
}
