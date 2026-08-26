package com.example.pharmacy.Controller;

import com.example.pharmacy.DTO.Store.StoreRequest;
import com.example.pharmacy.DTO.Store.StoreSummaryResponse;
import com.example.pharmacy.Service.StoreService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stores")
public class StoreController {
  private final StoreService storeService;

  public StoreController(StoreService storeService) {
    this.storeService = storeService;
  }

  @PostMapping("/add")
  public ResponseEntity<Void> addStore(
      @RequestBody StoreRequest storeRequest, Authentication authentication) {
    storeService.addStore(storeRequest, authentication.getName());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/getStores")
  public ResponseEntity<List<StoreSummaryResponse>> getStores(Authentication authentication) {
    return ResponseEntity.ok(storeService.getStores(authentication.getName()));
  }

  @PutMapping("/{storeId}")
  public ResponseEntity<Void> updateStoreDetails(
      @PathVariable Long storeId,
      @RequestBody StoreRequest storeRequest,
      Authentication authentication) {
    storeService.updateStoreDetails(
        storeId,
        storeRequest.getStoreName(),
        storeRequest.getStoreLocation(),
        authentication.getName());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{storeId}")
  public ResponseEntity<StoreSummaryResponse> getStore(
      @PathVariable Long storeId, Authentication authentication) {
    return ResponseEntity.ok(storeService.getStore(authentication.getName(), storeId));
  }
}
