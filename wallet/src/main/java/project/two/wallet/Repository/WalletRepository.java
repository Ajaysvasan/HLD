package project.two.wallet.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;
import project.two.wallet.DTO.Wallets.WalletBalance;
import project.two.wallet.Entity.Wallet;
import project.two.wallet.Exception.WalletNotFoundException;

@Repository
public class WalletRepository {
	private DataSource dataSource;

	public WalletRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Optional<Wallet> findByWalletId(Long wallet_id) {
		String query = "select wallet_id , owner_id , balance from wallets where wallet_id = ?;";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setLong(1, wallet_id);
			try (ResultSet result = statement.executeQuery()) {
				if (!result.next()) {
					return Optional.empty();
				}
				return Optional.of(mapRows(result));
			}
		} catch (SQLException e) {

			throw new RuntimeException("Failed to query wallet by id", e);
		}
	}

	public List<Wallet> findAllByUserId(Long userId) {
		String query = "select wallet_id, owner_id, balance from wallets where owner_id = ?;";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setLong(1, userId);
			try (ResultSet result = statement.executeQuery()) {
				List<Wallet> wallets = new ArrayList<>();
				while (result.next()) {
					wallets.add(mapRows(result));
				}
				return wallets;
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to fetch wallets for user", e);
		}
	}

	public boolean existsById(Long walletId) {
		String query = "select 1 from wallets where wallet_id = ? limit 1;";

		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setLong(1, walletId);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next();
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to check if email exists", e);
		}
	}

	public Optional<WalletBalance> getWalletBalance(Long walletId) {
		String query = "select wallet_id, balance from wallets where wallet_id = ?";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setLong(1, walletId);
			try (ResultSet result = statement.executeQuery()) {
				if (!result.next()) {
					return Optional.empty();
				}
				WalletBalance walletBalance = new WalletBalance();
				walletBalance.setWalletId(result.getLong("wallet_id"));
				walletBalance.setBalance(result.getLong("balance"));
				return Optional.of(walletBalance);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to fetch the balance from the wallet", e);
		}
	}

	public boolean existsByUserIdAndWalletId(Long userId, Long walletId) {

		String query = "select 1 from wallets where owner_id = ? and wallet_id = ? limit 1;";

		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setLong(1, userId);
			statement.setLong(2, walletId);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next();
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to check if email exists", e);
		}
	}

	public Wallet save(Wallet wallet) {
		String query = "insert into wallets (owner_id, balance) values (?, ?);";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(query,
						PreparedStatement.RETURN_GENERATED_KEYS)) {
			statement.setLong(1, wallet.getUserId());
			statement.setLong(2, wallet.getBalance());
			statement.executeUpdate();
			try (ResultSet result = statement.getGeneratedKeys()) {
				if (result.next()) {
					wallet.setWalletId(result.getLong(1));
				}
			}
			return wallet;

		} catch (SQLException e) {
			throw new RuntimeException("Failed to create the wallet", e);
		}
	}

	public long getBalanceForUpdate(Connection connection, Long walletId) throws SQLException {
		String query = "select balance from wallets where wallet_id = ? for update";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setLong(1, walletId);
			try (ResultSet result = statement.executeQuery()) {
				if (!result.next()) {
					throw new WalletNotFoundException("Wallet not found: " + walletId);
				}
				return result.getLong("balance");
			}
		}
	}

	public void updateBalance(Connection connection, Long walletId, long balance) throws SQLException {
		String query = "update wallets set balance = ? where wallet_id = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setLong(1, balance);
			statement.setLong(2, walletId);
			statement.executeUpdate();
		}
	}

	private Wallet mapRows(ResultSet result) throws SQLException {
		Wallet wallet = new Wallet();
		wallet.setUserId(result.getLong("owner_id"));
		wallet.setWalletId(result.getLong("wallet_id"));
		wallet.setBalance(result.getLong("balance"));
		return wallet;
	}
}
