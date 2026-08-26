package com.example.pharmacy.Entity;

public class Stocks {
  private Long stockId;
  private Long batchId;
  private Long storeId;
  private long stocksRecieved;
  private long stocksOut;
  private long stocksLeft;

  public void setStockId(Long stockId) {
    this.stockId = stockId;
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

  public void setStocksOut(long stocksOut) {
    this.stocksOut = stocksOut;
  }

  public void setStocksLeft(long stocksLeft) {
    this.stocksLeft = stocksLeft;
  }

  public Long getStockId() {
    return this.stockId;
  }

  public Long getBatchId() {
    return this.batchId;
  }

  public Long getStoreId() {
    return this.storeId;
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
