package com.fitnessclub.repository.jdbc;

import com.fitnessclub.db.Database;
import com.fitnessclub.model.FitnessClass;
import com.fitnessclub.repository.FitnessClassRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
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
    public FitnessClass create(FitnessClass fitnessClass) {
        String sql = """
                INSERT INTO classes(name, capacity, start_time)
                VALUES (?, ?, ?)
                RETURNING id, start_time
                """;
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fitnessClass.getName());
            ps.setInt(2, fitnessClass.getCapacity());
            ps.setObject(3, fitnessClass.getStartTime());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new FitnessClass(
                            rs.getInt("id"),
                            fitnessClass.getName(),
                            fitnessClass.getCapacity(),
                            rs.getObject("start_time", OffsetDateTime.class).toLocalDateTime()
                    );
                }
            }
            throw new SQLException("Insert failed");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create class", e);
        }
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
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch class", e);
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

    private FitnessClass map(ResultSet rs) throws SQLException {
        return new FitnessClass(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("capacity"),
                rs.getObject("start_time", OffsetDateTime.class).toLocalDateTime()
        );
    }
}
