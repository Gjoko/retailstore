package com.gjoko.retailstore.persistence.repositories;

import com.gjoko.retailstore.persistence.dao.ItemDto;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ItemsRepository extends MongoRepository<ItemDto, String> {

    @Query("{_id: { $in: ?0 } })")
    List<ItemDto> findByIds(List<String> ids, Sort sort);

    @Query("{name:'?0'}")
    ItemDto findByName(String name);
}
