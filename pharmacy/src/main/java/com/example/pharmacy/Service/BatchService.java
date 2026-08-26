package com.example.pharmacy.Service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacy.DTO.Batch.BatchRequest;
import com.example.pharmacy.DTO.Batch.BatchResponse;
import com.example.pharmacy.Entity.Batchs;
import com.example.pharmacy.Repository.BatchRepository;
import com.example.pharmacy.Repository.MedicineRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class BatchService {
  private final BatchRepository batchRepository;
  private final MedicineRepository medicineRepository;

  public BatchService(BatchRepository batchRepository, MedicineRepository medicineRepository) {
    this.batchRepository = batchRepository;
    this.medicineRepository = medicineRepository;
  }

  public void addBatch(BatchRequest batchRequest) {
    if (batchRequest.getMedicineId() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medicine id is required");
    }
    if (!medicineRepository.existsById(batchRequest.getMedicineId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine not found");
    }

    String manDate = batchRequest.getManDate();
    String expiryDate = batchRequest.getExpiryDate();
    if (manDate == null || manDate.isBlank() || expiryDate == null || expiryDate.isBlank()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Manufacture date and expiry date are required");
    }

    LocalDate parsedManDate;
    LocalDate parsedExpiryDate;
    try {
      parsedManDate = LocalDate.parse(manDate);
      parsedExpiryDate = LocalDate.parse(expiryDate);
    } catch (DateTimeParseException e) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Dates must be in yyyy-MM-dd format");
    }

    if (!parsedExpiryDate.isAfter(parsedManDate)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Expiry date must be after manufacture date");
    }

    Batchs batch = new Batchs();
    batch.setMedicineId(batchRequest.getMedicineId());
    batch.setManDate(manDate);
    batch.setExpiryDate(expiryDate);

    batchRepository.save(batch);
  }

  public BatchResponse getBatch(Long batchId) {
    return batchRepository
        .findByBatchId(batchId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found"));
  }

  public List<BatchResponse> getAllBatch() {
    return batchRepository.findAll();
  }
}
