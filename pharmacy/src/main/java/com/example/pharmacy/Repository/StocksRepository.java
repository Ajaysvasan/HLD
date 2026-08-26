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

import com.example.pharmacy.DTO.Stock.StockResponse;
import com.example.pharmacy.Entity.Stocks;

@Repository
public class StocksRepository {
  private final DataSource dataSource;

  public StocksRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Optional<StockResponse> findById(Long stockId) {
    String query =
        "select stk.stock_id, stk.stocks_recieved, stk.stocks_out, stk.stocks_left, "
            + "m.med_name, b.man_date, b.exp_date, s.store_name "
            + "from stocks stk "
            + "join batchs b on stk.batch_id = b.batch_id "
            + "join medicines m on b.med_id = m.med_id "
            + "join stores s on stk.store_id = s.store_id "
            + "where stk.stock_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, stockId);
      try (ResultSet result = statement.executeQuery()) {
        if (!result.next()) {
          return Optional.empty();
        }
        return Optional.of(mapRow(result));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to query stock by id", e);
    }
  }

  public List<StockResponse> findByStoreId(Long storeId) {
    List<StockResponse> stocks = new ArrayList<>();
    String query =
        "select stk.stock_id, stk.stocks_recieved, stk.stocks_out, stk.stocks_left, "
            + "m.med_name, b.man_date, b.exp_date, s.store_name "
            + "from stocks stk "
            + "join batchs b on stk.batch_id = b.batch_id "
            + "join medicines m on b.med_id = m.med_id "
            + "join stores s on stk.store_id = s.store_id "
            + "where stk.store_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, storeId);
      try (ResultSet result = statement.executeQuery()) {
        while (result.next()) {
          stocks.add(mapRow(result));
        }
      }
      return stocks;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to fetch stock for store", e);
    }
  }

  public List<StockResponse> findExpiredByStoreId(Long storeId) {
    List<StockResponse> stocks = new ArrayList<>();
    String query =
        "select stk.stock_id, stk.stocks_recieved, stk.stocks_out, stk.stocks_left, "
            + "m.med_name, b.man_date, b.exp_date, s.store_name "
            + "from stocks stk "
            + "join batchs b on stk.batch_id = b.batch_id "
            + "join medicines m on b.med_id = m.med_id "
            + "join stores s on stk.store_id = s.store_id "
            + "where stk.store_id = ? and b.exp_date < curdate() and stk.stocks_left > 0";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, storeId);
      try (ResultSet result = statement.executeQuery()) {
        while (result.next()) {
          stocks.add(mapRow(result));
        }
      }
      return stocks;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to fetch expired stock for store", e);
    }
  }

  public Optional<Stocks> findEntityById(Long stockId) {
    String query =
        "select stock_id, batch_id, store_id, stocks_recieved, stocks_out, stocks_left "
            + "from stocks where stock_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, stockId);
      try (ResultSet result = statement.executeQuery()) {
        if (!result.next()) {
          return Optional.empty();
        }
        Stocks stock = new Stocks();
        stock.setStockId(result.getLong("stock_id"));
        stock.setBatchId(result.getLong("batch_id"));
        stock.setStoreId(result.getLong("store_id"));
        stock.setStocksRecieved(result.getLong("stocks_recieved"));
        stock.setStocksOut(result.getLong("stocks_out"));
        stock.setStocksLeft(result.getLong("stocks_left"));
        return Optional.of(stock);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to query stock by id", e);
    }
  }

  public boolean recordStockOut(Long stockId, long quantity) {
    String query =
        "update stocks set stocks_out = stocks_out + ?, stocks_left = stocks_left - ? "
            + "where stock_id = ? and stocks_left >= ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, quantity);
      statement.setLong(2, quantity);
      statement.setLong(3, stockId);
      statement.setLong(4, quantity);
      int rowsAffected = statement.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to record stock out", e);
    }
  }

  public boolean existsById(Long stockId) {
    String query = "select 1 from stocks where stock_id = ?;";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, stockId);
      try (ResultSet result = statement.executeQuery()) {
        return result.next();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to query stock by id", e);
    }
  }

  public Stocks save(Stocks stock) {
    String query =
        "insert into stocks(batch_id , store_id , stocks_recieved , stocks_out , stocks_left)"
            + " values(?,?,?,?,?);";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement =
            connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      statement.setLong(1, stock.getBatchId());
      statement.setLong(2, stock.getStoreId());
      statement.setLong(3, stock.getStocksRecieved());
      statement.setLong(4, stock.getStocksOut());
      statement.setLong(5, stock.getStocksLeft());
      statement.executeUpdate();
      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          stock.setStockId(generatedKeys.getLong(1));
        }
      }
      return stock;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to save stock", e);
    }
  }

  private StockResponse mapRow(ResultSet result) throws SQLException {
    StockResponse stockResponse = new StockResponse();
    stockResponse.setStockId(result.getLong("stock_id"));
    stockResponse.setStoreName(result.getString("store_name"));
    stockResponse.setMedicineName(result.getString("med_name"));
    stockResponse.setManDate(result.getString("man_date"));
    stockResponse.setExpiryDate(result.getString("exp_date"));
    stockResponse.setStocksRecieved(result.getLong("stocks_recieved"));
    stockResponse.setStocksOut(result.getLong("stocks_out"));
    stockResponse.setStocksLeft(result.getLong("stocks_left"));
    return stockResponse;
  }
}
