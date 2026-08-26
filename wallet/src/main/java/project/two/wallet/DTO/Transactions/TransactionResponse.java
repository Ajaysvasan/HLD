package project.two.wallet.DTO.Transactions;

import java.time.LocalDate;

public class TransactionResponse {
	private Long transactionId;
	private Long sendWalletId;
	private Long receiverWalletId;
	private Long amount;
	private String type;
	private String status;
	private LocalDate date;

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Long getSendWalletId() {
		return sendWalletId;
	}

	public void setSendWalletId(Long sendWalletId) {
		this.sendWalletId = sendWalletId;
	}

	public Long getReceiverWalletId() {
		return receiverWalletId;
	}

	public void setReceiverWalletId(Long receiverWalletId) {
		this.receiverWalletId = receiverWalletId;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
}
