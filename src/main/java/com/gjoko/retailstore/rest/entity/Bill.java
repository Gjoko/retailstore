package com.gjoko.retailstore.rest.entity;

import java.util.Map;

public class Bill {

  private String id;
  private String userId;
  private Map<String, Integer> items;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Map<String, Integer> getItems() {
    return items;
  }

  public void setItems(Map<String, Integer> items) {
    this.items = items;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
