package project.two.wallet.Exception;

public class WalletNotFoundException extends RuntimeException {
	public WalletNotFoundException(String message) {
		super(message);
	}
}
