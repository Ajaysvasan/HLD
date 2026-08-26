package project.two.wallet.Exception;

public class InvalidTransactionException extends RuntimeException {
	public InvalidTransactionException(String message) {
		super(message);
	}
}
