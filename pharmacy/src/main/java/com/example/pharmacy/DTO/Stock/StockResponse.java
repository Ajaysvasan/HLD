package com.example.pharmacy.DTO.Stock;

public class StockResponse {
  private Long stockId;
  private String storeName;
  private String medicineName;
  private String manDate;
  private String expiryDate;
  private long stocksRecieved;
  private long stocksOut;
  private long stocksLeft;

  public void setStockId(Long stockId) {
    this.stockId = stockId;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  public void setMedicineName(String medicineName) {
    this.medicineName = medicineName;
  }

  public void setManDate(String manDate) {
    this.manDate = manDate;
  }

  public void setExpiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
  }

  public void setStocksRecieved(long stocksRecieved) {
    this.stocksRecieved = stocksRecieved;
  }

  public void setStocksOut(long stocksOut) {
    this.stocksOut = stocksOut;
  }

  public void setStocksLeft(long stocksLeft) {
    this.stocksLeft = stocksLeft;
  }

  public Long getStockId() {
    return this.stockId;
  }

  public String getStoreName() {
    return this.storeName;
  }

  public String getMedicineName() {
    return this.medicineName;
  }

  public String getManDate() {
    return this.manDate;
  }

  public String getExpiryDate() {
    return this.expiryDate;
  }

  public long getStocksRecieved() {
    return this.stocksRecieved;
  }

  public long getStocksOut() {
    return this.stocksOut;
  }

  public long getStocksLeft() {
    return this.stocksLeft;
  }
}
