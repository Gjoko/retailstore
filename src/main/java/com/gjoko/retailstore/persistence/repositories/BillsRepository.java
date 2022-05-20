package com.gjoko.retailstore.persistence.repositories;


import com.gjoko.retailstore.persistence.dao.BillDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface BillsRepository extends MongoRepository<BillDto, String> {

    @Query("{id:'?0'}")
    BillDto findBillDtoById(String id);

}
