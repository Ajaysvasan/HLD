package com.example.pharmacy.DTO.Store;

public class StoreResponse {
  private Long storeId;
  private String storeName;
  private String storeLocation;
  private String userName;

  public Long getStoreId() {
    return this.storeId;
  }

  public String getUserName() {
    return this.userName;
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

  public void setUserName(String userName) {
    this.userName = userName;
  }
}
