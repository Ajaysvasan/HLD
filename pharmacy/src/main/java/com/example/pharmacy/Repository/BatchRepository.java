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
import com.example.pharmacy.DTO.Batch.BatchResponse;
import com.example.pharmacy.Entity.Batchs;

@Repository
public class BatchRepository {
  private final DataSource dataSource;

  public BatchRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Optional<BatchResponse> findByBatchId(Long batchId) {
    String query =
        "select b.batch_id , b.man_date , b.exp_date , m.med_name "
            + "from batchs as b join medicines as m on b.med_id = m.med_id where b.batch_id = ?;";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, batchId);
      try (ResultSet result = statement.executeQuery()) {
        if (!result.next()) {
          return Optional.empty();
        }
        return Optional.of(mapRow(result));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to query batch by id", e);
    }
  }

  public List<BatchResponse> findAll() {
    List<BatchResponse> batches = new ArrayList<>();
    String query =
        "select b.batch_id , b.man_date , b.exp_date , m.med_name "
            + "from batchs as b join medicines as m on b.med_id = m.med_id;";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet result = statement.executeQuery()) {
      while (result.next()) {
        batches.add(mapRow(result));
      }
      return batches;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to fetch batches", e);
    }
  }

  public boolean existsById(Long batchId) {
    String query = "select 1 from batchs where batch_id = ? limit 1;";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, batchId);
      try (ResultSet resultSet = statement.executeQuery()) {
        return resultSet.next();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to query Batch by id", e);
    }
  }

  public Batchs save(Batchs batch) {
    String query = "insert into batchs(man_date , exp_date , med_id) values(? , ? , ?);";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement =
            connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, batch.getManDate());
      statement.setString(2, batch.getExpiryDate());
      statement.setLong(3, batch.getMedicineId());
      statement.executeUpdate();
      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          batch.setBatchId(generatedKeys.getLong(1));
        }
      }
      return batch;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to save Batch", e);
    }
  }

  private BatchResponse mapRow(ResultSet result) throws SQLException {
    BatchResponse batch = new BatchResponse();
    batch.setBatchId(result.getLong("batch_id"));
    batch.setMedicineName(result.getString("med_name"));
    batch.setManDate(result.getString("man_date"));
    batch.setExpiryDate(result.getString("exp_date"));
    return batch;
  }
}
