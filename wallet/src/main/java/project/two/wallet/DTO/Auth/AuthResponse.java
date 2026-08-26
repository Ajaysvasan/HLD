package project.two.wallet.DTO.Auth;

public class AuthResponse {
	private final String token;
	private final String tokenType = "Bearer";
	private final String userName;
	private final String email;

	public AuthResponse(String token, String userName, String email) {
		this.token = token;
		this.userName = userName;
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public String getTokenType() {
		return tokenType;
	}

	public String getUserName() {
		return userName;
	}

	public String getEmail() {
		return email;
	}
}
