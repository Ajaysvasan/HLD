package project.two.wallet.DTO.Transactions;

public class SendMoneyRequest {
	private Long fromWalletId;
	private Long toUserId;
	private Long toWalletId;
	private Long amount;

	public Long getFromWalletId() {
		return fromWalletId;
	}

	public void setFromWalletId(Long fromWalletId) {
		this.fromWalletId = fromWalletId;
	}

	public Long getToUserId() {
		return toUserId;
	}

	public void setToUserId(Long toUserId) {
		this.toUserId = toUserId;
	}

	public Long getToWalletId() {
		return toWalletId;
	}

	public void setToWalletId(Long toWalletId) {
		this.toWalletId = toWalletId;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}
}
