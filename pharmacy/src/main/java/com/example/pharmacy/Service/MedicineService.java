package com.example.pharmacy.Service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacy.DTO.Medicine.MedicineRequest;
import com.example.pharmacy.DTO.Medicine.MedicineResponse;
import com.example.pharmacy.Entity.Medicines;
import com.example.pharmacy.Repository.MedicineRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class MedicineService {
  private final MedicineRepository medicineRepository;
  private final StoreService storeService;

  public MedicineService(MedicineRepository medicineRepository, StoreService storeService) {
    this.medicineRepository = medicineRepository;
    this.storeService = storeService;
  }

  public void addMedicine(MedicineRequest medicineRequest) {
    String medicineName = medicineRequest.getMedicineName();
    if (medicineName == null || medicineName.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medicine name is required");
    }
    if (medicineRepository.existsByMedName(medicineName)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Medicine already exists");
    }

    Medicines medicine = new Medicines();
    medicine.setMedName(medicineName);

    medicineRepository.save(medicine);
  }

  public MedicineResponse getMedicine(Long medicineId) {
    Medicines medicine =
        medicineRepository
            .findByMedId(medicineId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine not found"));
    return toResponse(medicine);
  }

  public List<MedicineResponse> getAllMedicine() {
    List<Medicines> medicines = medicineRepository.findAll();
    List<MedicineResponse> response = new ArrayList<>();
    for (Medicines medicine : medicines) {
      response.add(toResponse(medicine));
    }
    return response;
  }

  public void changeMedicineName(Long medicineId, String newName) {
    if (newName == null || newName.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medicine name is required");
    }
    if (!medicineRepository.existsById(medicineId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine not found");
    }
    if (medicineRepository.existsByMedName(newName)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Medicine already exists");
    }
    medicineRepository.changeMedicineName(medicineId, newName);
  }

  public List<MedicineResponse> getMedicinesNotInStore(Long storeId, String ownerEmail) {
    storeService.verifyOwnership(storeId, ownerEmail);
    List<Medicines> medicines = medicineRepository.findNotInStore(storeId);
    List<MedicineResponse> response = new ArrayList<>();
    for (Medicines medicine : medicines) {
      response.add(toResponse(medicine));
    }
    return response;
  }

  public void deactivateMedicine(Long medicineId) {
    if (!medicineRepository.existsById(medicineId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine not found");
    }
    medicineRepository.deactivate(medicineId);
  }

  private MedicineResponse toResponse(Medicines medicine) {
    MedicineResponse medResponse = new MedicineResponse();
    medResponse.setMedId(medicine.getMedId());
    medResponse.setMedName(medicine.getMedName());
    medResponse.setActive(medicine.isActive());
    return medResponse;
  }
}
