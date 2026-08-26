package com.example.pharmacy.Entity;

public class Stores {
  private Long storeId;
  private String storeName;
  private String storeLocation;
  private Long userId;

  public Long getStoreId() {
    return this.storeId;
  }

  public Long getUserId() {
    return this.userId;
  }

  public String getStoreName() {
    return this.storeName;
  }

  public String getStoreLocation() {
    return this.storeLocation;
  }

  public void setStoreId(Long storeId) {
    this.storeId = storeId;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  public void setStoreLocation(String storeLocation) {
    this.storeLocation = storeLocation;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
}
