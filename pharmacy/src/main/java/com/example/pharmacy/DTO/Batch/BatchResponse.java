package com.example.pharmacy.DTO.Batch;

public class BatchResponse {
  private Long batchId;
  private String expiryDate;
  private String manDate;
  private String medicineName;

  public Long getBatchId() {
    return this.batchId;
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

  public void setExpiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
  }

  public void setManDate(String manDate) {
    this.manDate = manDate;
  }

  public void setMedicineName(String medicineName) {
    this.medicineName = medicineName;
  }

  public String getMedicineName() {
    return this.medicineName;
  }
}
