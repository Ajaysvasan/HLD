package project.two.wallet.DTO.Auth;

public class RegisterRequest {
	private String userName;
	private String email;
	private String password;
	private String confirmPassword;

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getUserName() {
		return this.userName;
	}

	public String getEmail() {
		return this.email;
	}

	public String getPassword() {
		return this.password;
	}

	public String getConfirmPassword() {
		return this.confirmPassword;
	}
}
