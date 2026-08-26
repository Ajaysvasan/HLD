package com.example.pharmacy.Service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacy.DTO.Store.StoreRequest;
import com.example.pharmacy.DTO.Store.StoreSummaryResponse;
import com.example.pharmacy.Entity.Stores;
import com.example.pharmacy.Entity.User;
import com.example.pharmacy.Repository.StoreRepository;
import com.example.pharmacy.Repository.UserRepository;
import java.util.ArrayList;
import java.util.List;

@Service
public class StoreService {
  private final StoreRepository storeRepository;
  private final UserRepository userRepository;

  public StoreService(StoreRepository storeRepository, UserRepository userRepository) {
    this.storeRepository = storeRepository;
    this.userRepository = userRepository;
  }

  public void addStore(StoreRequest storeRequest, String ownerEmail) {
    String storeName = storeRequest.getStoreName();
    if (storeName == null || storeName.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Store name is required");
    }

    User owner =
        userRepository
            .findByEmail(ownerEmail)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));

    Stores store = new Stores();
    store.setStoreName(storeName);
    store.setStoreLocation(storeRequest.getStoreLocation());
    store.setUserId(owner.getId());

    storeRepository.save(store);
  }

  public void verifyOwnership(Long storeId, String callerEmail) {
    User caller =
        userRepository
            .findByEmail(callerEmail)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));

    if (!storeRepository.existsByStoreIdAndUserId(storeId, caller.getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this store");
    }
  }

  public List<StoreSummaryResponse> getStores(String email) {
    User owner =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));

    List<Stores> stores = storeRepository.getStores(owner.getId());
    List<StoreSummaryResponse> response = new ArrayList<>();
    for (Stores store : stores) {
      response.add(toSummary(store));
    }
    return response;
  }

  public void updateStoreDetails(
      Long storeId, String storeName, String storeLocation, String ownerEmail) {
    if (storeName == null
        || storeName.isBlank()
        || storeLocation == null
        || storeLocation.isBlank()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Store name and store location can't be empty");
    }
    if (!storeRepository.existsById(storeId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "The store with the given id is not found");
    }
    verifyOwnership(storeId, ownerEmail);
    storeRepository.updateStoreDetails(storeId, storeName, storeLocation);
  }

  public StoreSummaryResponse getStore(String email, Long storeId) {
    User owner =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));

    Stores store =
        storeRepository
            .getStore(owner.getId(), storeId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

    return toSummary(store);
  }

  private StoreSummaryResponse toSummary(Stores store) {
    StoreSummaryResponse summary = new StoreSummaryResponse();
    summary.setStoreId(store.getStoreId());
    summary.setStoreName(store.getStoreName());
    summary.setStoreLocation(store.getStoreLocation());
    return summary;
  }
}
