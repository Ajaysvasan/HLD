package com.example.pharmacy.Controller;

import com.example.pharmacy.DTO.Auth.LoginRequest;
import com.example.pharmacy.DTO.Auth.RegisterRequest;
import com.example.pharmacy.Service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest login) {
    String token = authService.login(login);
    return ResponseEntity.ok(token);
  }

  @PostMapping("/register")
  public ResponseEntity<Void> register(@RequestBody RegisterRequest register) {
    authService.registerUser(register);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/me")
  public ResponseEntity<Void> deleteAccount(Authentication authentication) {
    authService.deleteAccount(authentication.getName());
    return ResponseEntity.noContent().build();
  }
}
