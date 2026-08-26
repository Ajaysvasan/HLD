package com.example.pharmacy.DTO.Store;

public class StoreRequest {
  private String storeName;
  private String storeLocation;

  public String getStoreName() {
    return this.storeName;
  }

  public String getStoreLocation() {
    return this.storeLocation;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  public void setStoreLocation(String storeLocation) {
    this.storeLocation = storeLocation;
  }
}
