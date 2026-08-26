package com.example.pharmacy.Controller;

import com.example.pharmacy.DTO.Batch.BatchRequest;
import com.example.pharmacy.DTO.Batch.BatchResponse;
import com.example.pharmacy.Service.BatchService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batches")
public class BatchController {
  private final BatchService batchService;

  public BatchController(BatchService batchService) {
    this.batchService = batchService;
  }

  @PostMapping("/add")
  public ResponseEntity<Void> addBatch(@RequestBody BatchRequest batchRequest) {
    batchService.addBatch(batchRequest);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/{batchId}")
  public ResponseEntity<BatchResponse> getBatch(@PathVariable Long batchId) {
    return ResponseEntity.ok(batchService.getBatch(batchId));
  }

  @GetMapping
  public ResponseEntity<List<BatchResponse>> getAllBatch() {
    return ResponseEntity.ok(batchService.getAllBatch());
  }
}
