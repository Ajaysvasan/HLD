package com.example.pharmacy.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import com.example.pharmacy.Entity.Medicines;

@Repository
public class MedicineRepository {
  private final DataSource dataSource;

  public MedicineRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Optional<Medicines> findByMedId(Long medicineId) {
    String query = "select med_id, med_name, is_active from medicines where med_id = ?;";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, medicineId);
      try (ResultSet result = statement.executeQuery()) {
        if (!result.next()) {
          return Optional.empty();
        }
        return Optional.of(mapRow(result));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to query medicine by id", e);
    }
  }

  public boolean existsById(Long medicineId) {
    String query = "select 1 from medicines where med_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, medicineId);
      try (ResultSet result = statement.executeQuery()) {
        return result.next();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to query medicine by id", e);
    }
  }

  public List<Medicines> findAll() {
    List<Medicines> medicines = new ArrayList<>();
    String query = "select med_id, med_name, is_active from medicines where is_active = true";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet result = statement.executeQuery()) {
      while (result.next()) {
        medicines.add(mapRow(result));
      }
      return medicines;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to fetch medicines", e);
    }
  }

  public List<Medicines> findNotInStore(Long storeId) {
    List<Medicines> medicines = new ArrayList<>();
    String query =
        "select med_id, med_name, is_active from medicines m "
            + "where m.is_active = true and not exists ("
            + "  select 1 from batchs b "
            + "  join stocks stk on stk.batch_id = b.batch_id "
            + "  where b.med_id = m.med_id and stk.store_id = ?"
            + ")";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, storeId);
      try (ResultSet result = statement.executeQuery()) {
        while (result.next()) {
          medicines.add(mapRow(result));
        }
      }
      return medicines;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to fetch medicines missing from store", e);
    }
  }

  public void changeMedicineName(Long medicineId, String newName) {
    String query = "update medicines set med_name = ? where med_id = ?;";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, newName);
      statement.setLong(2, medicineId);
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to update medicine name", e);
    }
  }

  public boolean existsByMedName(String medName) {
    String query = "select 1 from medicines where med_name = ? limit 1";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, medName);
      try (ResultSet result = statement.executeQuery()) {
        return result.next();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to check if medicine name exists", e);
    }
  }

  public Medicines save(Medicines medicine) {
    String query = "insert into medicines (med_name) values (?);";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement =
            connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, medicine.getMedName());
      statement.executeUpdate();
      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          medicine.setMedId(generatedKeys.getLong(1));
        }
      }
      return medicine;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to save medicine", e);
    }
  }

  public void deactivate(Long medId) {
    String query = "update medicines set is_active = false where med_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, medId);
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to deactivate medicine", e);
    }
  }

  private Medicines mapRow(ResultSet result) throws SQLException {
    Medicines medicine = new Medicines();
    medicine.setMedId(result.getLong("med_id"));
    medicine.setMedName(result.getString("med_name"));
    medicine.setActive(result.getBoolean("is_active"));
    return medicine;
  }
}
