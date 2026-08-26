package com.example.pharmacy.Service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacy.DTO.Stock.StockOutRequest;
import com.example.pharmacy.DTO.Stock.StockRequest;
import com.example.pharmacy.DTO.Stock.StockResponse;
import com.example.pharmacy.Entity.Stocks;
import com.example.pharmacy.Repository.BatchRepository;
import com.example.pharmacy.Repository.StocksRepository;

import java.util.List;

@Service
public class StockService {
  private final StocksRepository stocksRepository;
  private final BatchRepository batchRepository;
  private final StoreService storeService;

  public StockService(
      StocksRepository stocksRepository,
      BatchRepository batchRepository,
      StoreService storeService) {
    this.stocksRepository = stocksRepository;
    this.batchRepository = batchRepository;
    this.storeService = storeService;
  }

  public void addStock(StockRequest stockRequest, String ownerEmail) {
    if (stockRequest.getStoreId() == null || stockRequest.getBatchId() == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Store id and batch id are required");
    }

    storeService.verifyOwnership(stockRequest.getStoreId(), ownerEmail);

    if (!batchRepository.existsById(stockRequest.getBatchId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found");
    }

    if (stockRequest.getStocksRecieved() <= 0) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Stocks received must be greater than zero");
    }

    Stocks stock = new Stocks();
    stock.setStoreId(stockRequest.getStoreId());
    stock.setBatchId(stockRequest.getBatchId());
    stock.setStocksRecieved(stockRequest.getStocksRecieved());
    stock.setStocksOut(0);
    stock.setStocksLeft(stockRequest.getStocksRecieved());

    stocksRepository.save(stock);
  }

  public List<StockResponse> getStocksForStore(Long storeId, String ownerEmail) {
    storeService.verifyOwnership(storeId, ownerEmail);
    return stocksRepository.findByStoreId(storeId);
  }

  public List<StockResponse> getExpiredStocksForStore(Long storeId, String ownerEmail) {
    storeService.verifyOwnership(storeId, ownerEmail);
    return stocksRepository.findExpiredByStoreId(storeId);
  }

  public void recordStockOut(Long stockId, StockOutRequest stockOutRequest, String ownerEmail) {
    long quantity = stockOutRequest.getQuantity();
    if (quantity <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
    }

    Stocks stock =
        stocksRepository
            .findEntityById(stockId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found"));

    storeService.verifyOwnership(stock.getStoreId(), ownerEmail);

    boolean updated = stocksRepository.recordStockOut(stockId, quantity);
    if (!updated) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock left");
    }
  }
}
