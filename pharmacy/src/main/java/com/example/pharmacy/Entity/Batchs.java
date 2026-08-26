package com.example.pharmacy.Entity;

public class Batchs {
  private Long batchId;
  private Long medicineId;
  private String expiryDate;
  private String manDate;

  public Long getBatchId() {
    return this.batchId;
  }

  public Long getMedicineId() {
    return this.medicineId;
  }

  public String getExpiryDate() {
    return this.expiryDate;
  }

  public String getManDate() {
    return this.manDate;
  }

  public void setBatchId(Long batchId) {
    this.batchId = batchId;
  }

  public void setMedicineId(Long medicineId) {
    this.medicineId = medicineId;
  }

  public void setExpiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
  }

  public void setManDate(String manDate) {
    this.manDate = manDate;
  }
}
