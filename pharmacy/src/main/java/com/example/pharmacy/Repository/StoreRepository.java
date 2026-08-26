package com.example.pharmacy.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.example.pharmacy.DTO.Store.StoreResponse;
import com.example.pharmacy.Entity.Stores;

@Repository
public class StoreRepository {
  private final DataSource dataSource;

  public StoreRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Optional<StoreResponse> findByStoreId(Long storeId) {
    String query =
        "select s.store_id, s.store_name, s.store_location, u.name as user_name "
            + "from stores s join users u on u.id = s.user_id "
            + "where s.store_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, storeId);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (!resultSet.next()) {
          return Optional.empty();
        }
        return Optional.of(mapRow(resultSet));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to query store by id", e);
    }
  }

  public boolean existsById(Long storeId) {
    String query = "select 1 from stores where store_id = ? limit 1";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, storeId);
      try (ResultSet resultSet = statement.executeQuery()) {
        return resultSet.next();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to check if store exists", e);
    }
  }

  public boolean existsByStoreIdAndUserId(Long storeId, Long userId) {
    String query = "select 1 from stores where store_id = ? and user_id = ? limit 1";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, storeId);
      statement.setLong(2, userId);
      try (ResultSet resultSet = statement.executeQuery()) {
        return resultSet.next();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to check store ownership", e);
    }
  }

  public List<Stores> getStores(Long userId) {
    List<Stores> storeList = new ArrayList<>();
    String query = "select store_id , store_name , store_location from stores where user_id = ?;";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, userId);
      try (ResultSet result = statement.executeQuery()) {
        while (result.next()) {
          Stores store = new Stores();
          store.setStoreId(result.getLong("store_id"));
          store.setStoreName(result.getString("store_name"));
          store.setStoreLocation(result.getString("store_location"));
          storeList.add(store);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to fetch the details", e);
    }
    return storeList;
  }

  public Optional<Stores> getStore(Long userId, Long storeId) {
    String query =
        "select store_id , store_name , store_location from stores where user_id = ? and store_id ="
            + " ?;";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, userId);
      statement.setLong(2, storeId);
      try (ResultSet result = statement.executeQuery()) {
        if (!result.next()) {
          return Optional.empty();
        }
        Stores store = new Stores();
        store.setStoreId(storeId);
        store.setStoreName(result.getString("store_name"));
        store.setStoreLocation(result.getString("store_location"));
        return Optional.of(store);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find store", e);
    }
  }

  public void updateStoreDetails(Long storeId, String storeName, String storeLocation) {
    String query = "update stores set store_name = ? , store_location = ? where store_id = ?;";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, storeName);
      statement.setString(2, storeLocation);
      statement.setLong(3, storeId);
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to update the store name and location", e);
    }
  }

  public Stores save(Stores store) {
    String query = "insert into stores (store_name, store_location, user_id) values (?, ?, ?)";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement =
            connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, store.getStoreName());
      statement.setString(2, store.getStoreLocation());
      statement.setLong(3, store.getUserId());
      statement.executeUpdate();
      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          store.setStoreId(generatedKeys.getLong(1));
        }
      }
      return store;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to save store", e);
    }
  }

  private StoreResponse mapRow(ResultSet resultSet) throws SQLException {
    StoreResponse store = new StoreResponse();
    store.setStoreId(resultSet.getLong("store_id"));
    store.setStoreName(resultSet.getString("store_name"));
    store.setUserName(resultSet.getString("user_name"));
    store.setStoreLocation(resultSet.getString("store_location"));
    return store;
  }
}
