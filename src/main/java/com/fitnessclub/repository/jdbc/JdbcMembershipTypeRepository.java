package com.fitnessclub.repository.jdbc;

import com.fitnessclub.db.Database;
import com.fitnessclub.model.MembershipType;
import com.fitnessclub.repository.MembershipTypeRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMembershipTypeRepository implements MembershipTypeRepository {
    private final Database database;

    public JdbcMembershipTypeRepository(Database database) {
        this.database = database;
    }

    @Override
    public MembershipType create(MembershipType type) {
        String sql = """
                INSERT INTO membership_types(name, duration_days, price, visit_limit)
                VALUES (?, ?, ?, ?)
                RETURNING id
                """;
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type.getName());
            ps.setInt(2, type.getDurationDays());
            ps.setDouble(3, type.getPrice());
            if (type.getVisitLimit() == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, type.getVisitLimit());
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MembershipType(
                            rs.getInt("id"),
                            type.getName(),
                            type.getDurationDays(),
                            type.getPrice(),
                            type.getVisitLimit()
                    );
                }
            }
            throw new SQLException("Insert did not return id");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create membership type", e);
        }
    }

    @Override
    public Optional<MembershipType> findById(Integer id) {
        String sql = "SELECT * FROM membership_types WHERE id = ?";
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
            throw new IllegalStateException("Failed to load membership type", e);
        }
    }

    @Override
    public List<MembershipType> findAll() {
        String sql = "SELECT * FROM membership_types ORDER BY id";
        try (Connection conn = database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            List<MembershipType> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load membership types", e);
        }
    }

    private MembershipType map(ResultSet rs) throws SQLException {
        return new MembershipType(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("duration_days"),
                rs.getDouble("price"),
                (Integer) rs.getObject("visit_limit")
        );
    }
}
