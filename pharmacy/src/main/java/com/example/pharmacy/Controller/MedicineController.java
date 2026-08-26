package com.example.pharmacy.Controller;

import com.example.pharmacy.DTO.Medicine.MedicineNameChange;
import com.example.pharmacy.DTO.Medicine.MedicineRequest;
import com.example.pharmacy.DTO.Medicine.MedicineResponse;
import com.example.pharmacy.Service.MedicineService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {
  private final MedicineService medicineService;

  public MedicineController(MedicineService medicineService) {
    this.medicineService = medicineService;
  }

  @PostMapping("/add")
  public ResponseEntity<Void> addMedicine(@RequestBody MedicineRequest medicineRequest) {
    medicineService.addMedicine(medicineRequest);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/{medId}")
  public ResponseEntity<MedicineResponse> getMedicine(@PathVariable Long medId) {
    return ResponseEntity.ok(medicineService.getMedicine(medId));
  }

  @GetMapping
  public ResponseEntity<List<MedicineResponse>> getAllMedicine() {
    return ResponseEntity.ok(medicineService.getAllMedicine());
  }

  @GetMapping("/store/{storeId}/missing")
  public ResponseEntity<List<MedicineResponse>> getMedicinesNotInStore(
      @PathVariable Long storeId, Authentication authentication) {
    return ResponseEntity.ok(
        medicineService.getMedicinesNotInStore(storeId, authentication.getName()));
  }

  @DeleteMapping("/{medId}")
  public ResponseEntity<Void> deactivateMedicine(@PathVariable Long medId) {
    medicineService.deactivateMedicine(medId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{medId}")
  public ResponseEntity<Void> changeMedicineName(
      @PathVariable Long medId, @RequestBody MedicineNameChange medicineNameChange) {
    medicineService.changeMedicineName(medId, medicineNameChange.getMedicineName());
    return ResponseEntity.noContent().build();
  }
}
