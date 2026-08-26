package com.example.pharmacy.Controller;

import com.example.pharmacy.DTO.Stock.StockOutRequest;
import com.example.pharmacy.DTO.Stock.StockRequest;
import com.example.pharmacy.DTO.Stock.StockResponse;
import com.example.pharmacy.Service.StockService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
  private final StockService stockService;

  public StockController(StockService stockService) {
    this.stockService = stockService;
  }

  @PostMapping("/add")
  public ResponseEntity<Void> addStock(
      @RequestBody StockRequest stockRequest, Authentication authentication) {
    stockService.addStock(stockRequest, authentication.getName());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/store/{storeId}")
  public ResponseEntity<List<StockResponse>> getStocksForStore(
      @PathVariable Long storeId, Authentication authentication) {
    return ResponseEntity.ok(stockService.getStocksForStore(storeId, authentication.getName()));
  }

  @GetMapping("/store/{storeId}/expired")
  public ResponseEntity<List<StockResponse>> getExpiredStocksForStore(
      @PathVariable Long storeId, Authentication authentication) {
    return ResponseEntity.ok(
        stockService.getExpiredStocksForStore(storeId, authentication.getName()));
  }

  @PostMapping("/{stockId}/out")
  public ResponseEntity<Void> recordStockOut(
      @PathVariable Long stockId,
      @RequestBody StockOutRequest stockOutRequest,
      Authentication authentication) {
    stockService.recordStockOut(stockId, stockOutRequest, authentication.getName());
    return ResponseEntity.ok().build();
  }
}
