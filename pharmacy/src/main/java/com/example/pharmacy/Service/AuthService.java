package com.example.pharmacy.Service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacy.DTO.Auth.LoginRequest;
import com.example.pharmacy.DTO.Auth.RegisterRequest;
import com.example.pharmacy.Repository.UserRepository;
import com.example.pharmacy.Entity.User;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordService passwordService;
  private final JwtService jwtService;

  public AuthService(
      UserRepository userRepository, PasswordService passwordService, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordService = passwordService;
    this.jwtService = jwtService;
  }

  public void registerUser(RegisterRequest registerRequest) {
    String password = registerRequest.getPassword();
    String confirmPassword = registerRequest.getConfirmPassword();
    if (password == null || !password.equals(confirmPassword)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
    }
    if (userRepository.existsByEmail(registerRequest.getEmail())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
    }

    User user = new User();
    user.setEmail(registerRequest.getEmail());
    user.setUserName(registerRequest.getName());
    user.setPassword(passwordService.hash(password));
    user.setPhoneNumber(registerRequest.getPhoneNumber());
    userRepository.save(user);
  }

  public String login(LoginRequest loginRequest) {
    User user =
        userRepository
            .findByEmail(loginRequest.getEmail())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid email or password"));

    if (!passwordService.matches(loginRequest.getPassword(), user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    return jwtService.generateToken(user.getEmail());
  }

  public void deleteAccount(String email) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));
    userRepository.delete(user.getId());
  }
}
