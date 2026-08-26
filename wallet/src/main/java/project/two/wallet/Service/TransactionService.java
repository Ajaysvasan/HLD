package project.two.wallet.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.stereotype.Service;
import project.two.wallet.DTO.Transactions.DepositRequest;
import project.two.wallet.DTO.Transactions.SendMoneyRequest;
import project.two.wallet.DTO.Transactions.TransactionResponse;
import project.two.wallet.DTO.Transactions.WithdrawRequest;
import project.two.wallet.Entity.Transaction;
import project.two.wallet.Entity.TransactionType;
import project.two.wallet.Exception.InsufficientBalanceException;
import project.two.wallet.Exception.InvalidTransactionException;
import project.two.wallet.Exception.WalletNotFoundException;
import project.two.wallet.Repository.TransactionRepository;
import project.two.wallet.Repository.WalletRepository;

@Service
public class TransactionService {
	private static final String STATUS_SUCCESS = "SUCCESS";

	private final DataSource dataSource;
	private final WalletRepository walletRepository;
	private final TransactionRepository transactionRepository;

	public TransactionService(DataSource dataSource, WalletRepository walletRepository,
			TransactionRepository transactionRepository) {
		this.dataSource = dataSource;
		this.walletRepository = walletRepository;
		this.transactionRepository = transactionRepository;
	}

	public TransactionResponse send(Long fromUserId, SendMoneyRequest request) {
		validateAmount(request.getAmount());
		Long fromWalletId = require(request.getFromWalletId(), "fromWalletId");
		Long toUserId = require(request.getToUserId(), "toUserId");
		Long toWalletId = require(request.getToWalletId(), "toWalletId");
		if (fromWalletId.equals(toWalletId)) {
			throw new InvalidTransactionException("Cannot transfer to the same wallet");
		}
		if (!walletRepository.existsByUserIdAndWalletId(fromUserId, fromWalletId)) {
			throw new WalletNotFoundException("Wallet not found: " + fromWalletId);
		}
		if (!walletRepository.existsByUserIdAndWalletId(toUserId, toWalletId)) {
			throw new WalletNotFoundException("Wallet not found: " + toWalletId);
		}

		return runInTransaction(connection -> {
			Map<Long, Long> balances = lockBalances(connection, fromWalletId, toWalletId);
			long fromBalance = balances.get(fromWalletId);
			long toBalance = balances.get(toWalletId);
			if (fromBalance < request.getAmount()) {
				throw new InsufficientBalanceException("Insufficient balance in wallet: " + fromWalletId);
			}
			walletRepository.updateBalance(connection, fromWalletId, fromBalance - request.getAmount());
			walletRepository.updateBalance(connection, toWalletId, toBalance + request.getAmount());
			Transaction transaction = newTransaction(fromWalletId, toWalletId, request.getAmount(),
					TransactionType.TRANSFER);
			return toResponse(transactionRepository.save(connection, transaction));
		});
	}

	public TransactionResponse deposit(Long userId, DepositRequest request) {
		validateAmount(request.getAmount());
		Long walletId = require(request.getWalletId(), "walletId");
		if (!walletRepository.existsByUserIdAndWalletId(userId, walletId)) {
			throw new WalletNotFoundException("Wallet not found: " + walletId);
		}
		return runInTransaction(connection -> {
			long balance = walletRepository.getBalanceForUpdate(connection, walletId);
			walletRepository.updateBalance(connection, walletId, balance + request.getAmount());
			Transaction transaction = newTransaction(walletId, walletId, request.getAmount(), TransactionType.DEPOSIT);
			return toResponse(transactionRepository.save(connection, transaction));
		});
	}

	public TransactionResponse withdraw(Long userId, WithdrawRequest request) {
		validateAmount(request.getAmount());
		Long walletId = require(request.getWalletId(), "walletId");
		if (!walletRepository.existsByUserIdAndWalletId(userId, walletId)) {
			throw new WalletNotFoundException("Wallet not found: " + walletId);
		}
		return runInTransaction(connection -> {
			long balance = walletRepository.getBalanceForUpdate(connection, walletId);
			if (balance < request.getAmount()) {
				throw new InsufficientBalanceException("Insufficient balance in wallet: " + walletId);
			}
			walletRepository.updateBalance(connection, walletId, balance - request.getAmount());
			Transaction transaction = newTransaction(walletId, walletId, request.getAmount(), TransactionType.WITHDRAW);
			return toResponse(transactionRepository.save(connection, transaction));
		});
	}

	public List<TransactionResponse> getHistory(Long userId, Long otherUserId, Long otherWalletId, Long walletId,
			boolean self, String sort) {
		boolean hasOtherUser = otherUserId != null;
		boolean hasOtherWallet = otherWalletId != null;
		boolean hasWallet = walletId != null;

		if (hasOtherWallet && !hasOtherUser) {
			throw new InvalidTransactionException("otherWalletId requires otherUserId");
		}
		if (self && (hasOtherUser || hasWallet)) {
			throw new InvalidTransactionException("self cannot be combined with otherUserId or walletId");
		}
		if (hasWallet && hasOtherUser) {
			throw new InvalidTransactionException("walletId cannot be combined with otherUserId");
		}

		String direction = normalizeSort(sort);

		if (self) {
			return map(transactionRepository.findSelfTransactions(userId, direction));
		}
		if (hasOtherUser && hasOtherWallet) {
			if (!walletRepository.existsByUserIdAndWalletId(otherUserId, otherWalletId)) {
				throw new WalletNotFoundException("Wallet not found: " + otherWalletId);
			}
			return map(transactionRepository.findByUserAndWallet(userId, otherWalletId, direction));
		}
		if (hasOtherUser) {
			return map(transactionRepository.findToUser(userId, otherUserId, direction));
		}
		if (hasWallet) {
			if (!walletRepository.existsByUserIdAndWalletId(userId, walletId)) {
				throw new WalletNotFoundException("Wallet not found: " + walletId);
			}
			return map(transactionRepository.findByWallet(walletId, direction));
		}
		return map(transactionRepository.findAllForUser(userId, direction));
	}

	private Map<Long, Long> lockBalances(Connection connection, Long walletA, Long walletB) throws SQLException {
		Long first = walletA < walletB ? walletA : walletB;
		Long second = walletA < walletB ? walletB : walletA;
		Map<Long, Long> balances = new HashMap<>();
		balances.put(first, walletRepository.getBalanceForUpdate(connection, first));
		balances.put(second, walletRepository.getBalanceForUpdate(connection, second));
		return balances;
	}

	private Transaction newTransaction(Long sendWalletId, Long receiverWalletId, Long amount, TransactionType type) {
		Transaction transaction = new Transaction();
		transaction.setSendWalletId(sendWalletId);
		transaction.setReceiverWalletId(receiverWalletId);
		transaction.setAmount(amount);
		transaction.setType(type);
		transaction.setStatus(STATUS_SUCCESS);
		transaction.setDate(LocalDate.now());
		return transaction;
	}

	private void validateAmount(Long amount) {
		if (amount == null || amount <= 0) {
			throw new InvalidTransactionException("Amount must be greater than zero");
		}
	}

	private Long require(Long value, String field) {
		if (value == null) {
			throw new InvalidTransactionException(field + " is required");
		}
		return value;
	}

	private String normalizeSort(String sort) {
		if (sort == null || sort.isBlank()) {
			return null;
		}
		if (sort.equalsIgnoreCase("asc")) {
			return "ASC";
		}
		if (sort.equalsIgnoreCase("desc")) {
			return "DESC";
		}
		throw new InvalidTransactionException("sort must be 'asc' or 'desc'");
	}

	private List<TransactionResponse> map(List<Transaction> transactions) {
		return transactions.stream().map(this::toResponse).collect(Collectors.toList());
	}

	private TransactionResponse toResponse(Transaction transaction) {
		TransactionResponse response = new TransactionResponse();
		response.setTransactionId(transaction.getTransactionId());
		response.setSendWalletId(transaction.getSendWalletId());
		response.setReceiverWalletId(transaction.getReceiverWalletId());
		response.setAmount(transaction.getAmount());
		response.setType(transaction.getType().name());
		response.setStatus(transaction.getStatus());
		response.setDate(transaction.getDate());
		return response;
	}

	@FunctionalInterface
	private interface TransactionalOperation<T> {
		T execute(Connection connection) throws SQLException;
	}

	private <T> T runInTransaction(TransactionalOperation<T> operation) {
		try (Connection connection = dataSource.getConnection()) {
			connection.setAutoCommit(false);
			try {
				T result = operation.execute(connection);
				connection.commit();
				return result;
			} catch (RuntimeException e) {
				connection.rollback();
				throw e;
			} catch (SQLException e) {
				connection.rollback();
				throw new RuntimeException("Transaction failed", e);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to access the database", e);
		}
	}
}
