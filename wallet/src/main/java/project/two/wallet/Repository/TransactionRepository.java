package project.two.wallet.Repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;
import project.two.wallet.Entity.Transaction;
import project.two.wallet.Entity.TransactionType;

@Repository
public class TransactionRepository {
	private static final String SELECT_COLUMNS = "select transaction_id, send_wallet_id, receiver_wallet_id, "
			+ "amount, type, status, date from transactions ";

	private final DataSource dataSource;

	public TransactionRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Transaction save(Connection connection, Transaction transaction) throws SQLException {
		String query = "insert into transactions (send_wallet_id, receiver_wallet_id, amount, type, status, date) "
				+ "values (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(query,
				PreparedStatement.RETURN_GENERATED_KEYS)) {
			statement.setLong(1, transaction.getSendWalletId());
			statement.setLong(2, transaction.getReceiverWalletId());
			statement.setLong(3, transaction.getAmount());
			statement.setString(4, transaction.getType().name());
			statement.setString(5, transaction.getStatus());
			statement.setDate(6, Date.valueOf(transaction.getDate()));
			statement.executeUpdate();
			try (ResultSet result = statement.getGeneratedKeys()) {
				if (result.next()) {
					transaction.setTransactionId(result.getLong(1));
				}
			}
			return transaction;
		}
	}

	public List<Transaction> findAllForUser(Long userId, String direction) {
		String query = SELECT_COLUMNS + "where send_wallet_id in (select wallet_id from wallets where owner_id = ?)"
				+ orderBy(direction);
		return query(query, statement -> statement.setLong(1, userId));
	}

	public List<Transaction> findToUser(Long userId, Long otherUserId, String direction) {
		String query = SELECT_COLUMNS + "where send_wallet_id in (select wallet_id from wallets where owner_id = ?) "
				+ "and receiver_wallet_id in (select wallet_id from wallets where owner_id = ?) "
				+ "and type = 'TRANSFER'" + orderBy(direction);
		return query(query, statement -> {
			statement.setLong(1, userId);
			statement.setLong(2, otherUserId);
		});
	}

	public List<Transaction> findSelfTransactions(Long userId, String direction) {
		String query = SELECT_COLUMNS + "where send_wallet_id in (select wallet_id from wallets where owner_id = ?) "
				+ "and type in ('DEPOSIT','WITHDRAW')" + orderBy(direction);
		return query(query, statement -> statement.setLong(1, userId));
	}

	public List<Transaction> findByWallet(Long walletId, String direction) {
		String query = SELECT_COLUMNS + "where send_wallet_id = ?" + orderBy(direction);
		return query(query, statement -> statement.setLong(1, walletId));
	}

	public List<Transaction> findByUserAndWallet(Long userId, Long receiverWalletId, String direction) {
		String query = SELECT_COLUMNS + "where send_wallet_id in (select wallet_id from wallets where owner_id = ?) "
				+ "and receiver_wallet_id = ?" + orderBy(direction);
		return query(query, statement -> {
			statement.setLong(1, userId);
			statement.setLong(2, receiverWalletId);
		});
	}

	private List<Transaction> query(String query, StatementBinder binder) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {
			binder.bind(statement);
			try (ResultSet result = statement.executeQuery()) {
				List<Transaction> transactions = new ArrayList<>();
				while (result.next()) {
					transactions.add(mapRow(result));
				}
				return transactions;
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to query transactions", e);
		}
	}

	private String orderBy(String direction) {
		return direction == null ? "" : " order by date " + direction + ", transaction_id " + direction;
	}

	private Transaction mapRow(ResultSet result) throws SQLException {
		Transaction transaction = new Transaction();
		transaction.setTransactionId(result.getLong("transaction_id"));
		transaction.setSendWalletId(result.getLong("send_wallet_id"));
		transaction.setReceiverWalletId(result.getLong("receiver_wallet_id"));
		transaction.setAmount(result.getLong("amount"));
		transaction.setType(TransactionType.valueOf(result.getString("type")));
		transaction.setStatus(result.getString("status"));
		transaction.setDate(result.getDate("date").toLocalDate());
		return transaction;
	}

	@FunctionalInterface
	private interface StatementBinder {
		void bind(PreparedStatement statement) throws SQLException;
	}
}
