package com.example.pharmacy.DTO.Medicine;

public class MedicineResponse {
  private Long medId;
  private String medName;
  private boolean active;

  public void setMedId(Long medId) {
    this.medId = medId;
  }

  public void setMedName(String medName) {
    this.medName = medName;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public String getMedName() {
    return this.medName;
  }

  public Long getMedId() {
    return this.medId;
  }

  public boolean isActive() {
    return this.active;
  }
}
