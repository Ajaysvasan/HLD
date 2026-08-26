package com.example.pharmacy.Repository;

import com.example.pharmacy.Entity.User;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class UserRepository {
  private final DataSource dataSource;

  public UserRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Optional<User> findByEmail(String email) {
    String sql = "SELECT id, name, email, phone_number, password FROM users WHERE email = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, email);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (!resultSet.next()) {
          return Optional.empty();
        }
        return Optional.of(mapRow(resultSet));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to query user by email", e);
    }
  }

  public boolean existsByEmail(String email) {
    String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
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

  public User save(User user) {
    String sql = "INSERT INTO users (name, email, phone_number, password) VALUES (?, ?, ?, ?)";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement =
            connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, user.getName());
      statement.setString(2, user.getEmail());
      statement.setString(3, user.getPhoneNumber());
      statement.setString(4, user.getPassword());
      statement.executeUpdate();
      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          user.setId(generatedKeys.getLong(1));
        }
      }
      return user;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to save user", e);
    }
  }

  public void delete(Long userId) {
    String sql = "DELETE FROM users WHERE id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, userId);
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to delete user", e);
    }
  }

  private User mapRow(ResultSet resultSet) throws SQLException {
    User user = new User();
    user.setId(resultSet.getLong("id"));
    user.setUserName(resultSet.getString("name"));
    user.setEmail(resultSet.getString("email"));
    user.setPhoneNumber(resultSet.getString("phone_number"));
    user.setPassword(resultSet.getString("password"));
    return user;
  }
}
