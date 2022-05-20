package com.gjoko.retailstore.rest.entity;

import com.gjoko.retailstore.persistence.enums.ItemType;

public class Item {

    private String id;
    private String name;
    private ItemType type;
    private String price;

    public Item() {
    }

    public Item(String id, String name, ItemType type, String price) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
}
