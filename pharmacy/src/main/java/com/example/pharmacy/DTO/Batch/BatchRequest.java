package com.example.pharmacy.DTO.Batch;

public class BatchRequest {
  private Long medicineId;
  private String manDate;
  private String expiryDate;

  public Long getMedicineId() {
    return this.medicineId;
  }

  public String getManDate() {
    return this.manDate;
  }

  public String getExpiryDate() {
    return this.expiryDate;
  }

  public void setMedicineId(Long medicineId) {
    this.medicineId = medicineId;
  }

  public void setManDate(String manDate) {
    this.manDate = manDate;
  }

  public void setExpiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
  }
}
