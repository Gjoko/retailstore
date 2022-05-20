package com.gjoko.retailstore.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjoko.retailstore.rest.entity.Bill;
import com.gjoko.retailstore.rest.entity.Item;
import com.gjoko.retailstore.rest.entity.User;

public class ConvertorHelper {
    final static ObjectMapper mapper = new ObjectMapper();

    public static String asJsonString(final Object obj) {
        try {
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Item asItem (final byte[] bytes) {
        try {
            return  mapper.readValue(bytes, Item.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Bill asBill(final byte[] bytes) {
        try {
            return  mapper.readValue(bytes, Bill.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static User asUser(final byte[] bytes) {
        try {
            return  mapper.readValue(bytes, User.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
