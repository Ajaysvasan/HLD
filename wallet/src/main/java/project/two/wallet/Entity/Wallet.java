package project.two.wallet.Entity;

public class Wallet {
	private Long walletId;
	private Long userId;
	private Long balance;

	public void setWalletId(Long walletId) {
		this.walletId = walletId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public Long getUserId() {
		return this.userId;
	}

	public Long getWalletId() {
		return this.walletId;
	}

	public Long getBalance() {
		return this.balance;
	}
}
