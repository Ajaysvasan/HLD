package project.two.wallet.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import project.two.wallet.Entity.User;

@Repository
public class UserRepository {
  private DataSource dataSource;

  public UserRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Cacheable(value = "users", key = "#email", unless = "#result == null")
  public Optional<User> findByEmail(String email) {
    String query =
        "select user_id , user_name , user_email , password from users where user_email = ?;";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, email);
      try (ResultSet result = statement.executeQuery()) {
        if (!result.next()) {
          return Optional.empty();
        }
        return Optional.of(mapRows(result));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to query user by email", e);
    }
  }

  public boolean existsByEmail(String email) {

    String sql = "SELECT 1 FROM users WHERE user_email = ? LIMIT 1";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, email);
      try (ResultSet resultSet = statement.executeQuery()) {
        return resultSet.next();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to check if email exists", e);
    }
  }

  @CacheEvict(value = "users", key = "#user.email")
  public User save(User user) {
    String query = "insert into users(user_name ,user_email , password) values (? , ? , ?);";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement =
            connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, user.getUserName());
      statement.setString(2, user.getEmail());
      statement.setString(3, user.getPassword());
      statement.executeUpdate();
      try (ResultSet result = statement.getGeneratedKeys()) {
        if (result.next()) {
          user.setUserId(result.getLong(1));
        }
      }
      return user;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create the user", e);
    }
  }

  private User mapRows(ResultSet result) throws SQLException {
    User user = new User();
    user.setUserId(result.getLong("user_id"));
    user.setUserName(result.getString("user_name"));
    user.setEmail(result.getString("user_email"));
    user.setPassword(result.getString("password"));
    return user;
  }
}
