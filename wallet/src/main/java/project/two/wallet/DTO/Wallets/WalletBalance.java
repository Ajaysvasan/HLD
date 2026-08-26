package project.two.wallet.DTO.Wallets;

public class WalletBalance {

	private Long walletId;
	private Long balance;

	public void setWalletId(Long walletId) {
		this.walletId = walletId;
	}

	public Long getWalletId() {
		return this.walletId;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public Long getBalance() {
		return this.balance;
	}
}
