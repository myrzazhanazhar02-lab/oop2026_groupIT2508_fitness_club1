package com.fitnessclub.repository.jdbc;

import com.fitnessclub.db.Database;
import com.fitnessclub.model.FitnessClass;
import com.fitnessclub.repository.FitnessClassRepository;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcFitnessClassRepository implements FitnessClassRepository {

    private final Database database;

    public JdbcFitnessClassRepository(Database database) {
        this.database = database;
    }

    @Override
    public Optional<FitnessClass> findById(Integer id) {
        String sql = "SELECT * FROM classes WHERE id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch class by id", e);
        }
    }

    @Override
    public List<FitnessClass> findAll() {
        String sql = "SELECT * FROM classes ORDER BY start_time";
        try (Connection conn = database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            List<FitnessClass> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch classes", e);
        }
    }

    @Override
    public void save(FitnessClass entity) {
        String sql = "INSERT INTO classes(name, capacity, start_time) VALUES (?, ?, ?)";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getName());
            ps.setInt(2, entity.getCapacity());
            ps.setObject(3, entity.getStartTime()); // если LocalDateTime/OffsetDateTime — JDBC сам разрулит

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save class", e);
        }
    }

    @Override
    public void update(FitnessClass entity) {
        String sql = "UPDATE classes SET name = ?, capacity = ?, start_time = ? WHERE id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getName());
            ps.setInt(2, entity.getCapacity());
            ps.setObject(3, entity.getStartTime());
            ps.setInt(4, entity.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update class", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM classes WHERE id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete class by id", e);
        }
    }

    private FitnessClass map(ResultSet rs) throws SQLException {
        return new FitnessClass(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("capacity"),
                rs.getObject("start_time", OffsetDateTime.class).toLocalDateTime()
        );
    }
}
