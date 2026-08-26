package project.two.wallet.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.two.wallet.DTO.Auth.AuthResponse;
import project.two.wallet.DTO.Auth.LoginRequest;
import project.two.wallet.DTO.Auth.RegisterRequest;
import project.two.wallet.Entity.User;
import project.two.wallet.Exception.EmailAlreadyExistsException;
import project.two.wallet.Exception.InvalidCredentialsException;
import project.two.wallet.Exception.PasswordMismatchException;
import project.two.wallet.Repository.UserRepository;
import project.two.wallet.Security.JwtService;

@Service
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	public AuthResponse register(RegisterRequest request) {
		if (!request.getPassword().equals(request.getConfirmPassword())) {
			throw new PasswordMismatchException("Password and confirm password do not match");
		}
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("Email is already registered: " + request.getEmail());
		}

		User user = new User();
		user.setUserName(request.getUserName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		User savedUser = userRepository.save(user);

		String token = jwtService.generateToken(savedUser.getEmail());
		return new AuthResponse(token, savedUser.getUserName(), savedUser.getEmail());
	}

	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid email or password");
		}

		String token = jwtService.generateToken(user.getEmail());
		return new AuthResponse(token, user.getUserName(), user.getEmail());
	}
}
