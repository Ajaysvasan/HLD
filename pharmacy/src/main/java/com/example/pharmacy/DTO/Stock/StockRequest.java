package com.example.pharmacy.DTO.Stock;

public class StockRequest {
  private Long batchId;
  private Long storeId;
  private long stocksRecieved;

  public Long getBatchId() {
    return this.batchId;
  }

  public Long getStoreId() {
    return this.storeId;
  }

  public long getStocksRecieved() {
    return this.stocksRecieved;
  }

  public void setBatchId(Long batchId) {
    this.batchId = batchId;
  }

  public void setStoreId(Long storeId) {
    this.storeId = storeId;
  }

  public void setStocksRecieved(long stocksRecieved) {
    this.stocksRecieved = stocksRecieved;
  }
}
