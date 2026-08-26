package com.example.pharmacy.Entity;

public class User {
  private Long id;
  private String name;
  private String email;
  private String phoneNumber;
  private String password;

  public User() {}

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getEmail() {
    return this.email;
  }

  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  public String getPassword() {
    return this.password;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setUserName(String name) {
    this.name = name;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
