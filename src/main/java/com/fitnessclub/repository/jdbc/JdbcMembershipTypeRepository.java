package com.fitnessclub.repository.jdbc;

import com.fitnessclub.db.Database;
import com.fitnessclub.model.MembershipType;
import com.fitnessclub.repository.MembershipTypeRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMembershipTypeRepository implements MembershipTypeRepository {

    private final Database database;

    public JdbcMembershipTypeRepository(Database database) {
        this.database = database;
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
            throw new IllegalStateException("Failed to fetch membership type by id", e);
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
            throw new IllegalStateException("Failed to fetch membership types", e);
        }
    }

    @Override
    public void save(MembershipType entity) {
        String sql = "INSERT INTO membership_types(name, price, duration_days) VALUES (?, ?, ?)";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getName());
            ps.setDouble(2, entity.getPrice());
            ps.setInt(3, entity.getDurationDays());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save membership type", e);
        }
    }

    @Override
    public void update(MembershipType entity) {
        String sql = "UPDATE membership_types SET name = ?, price = ?, duration_days = ? WHERE id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getName());
            ps.setDouble(2, entity.getPrice());
            ps.setInt(3, entity.getDurationDays());
            ps.setInt(4, entity.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update membership type", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM membership_types WHERE id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete membership type", e);
        }
    }

    private MembershipType map(ResultSet rs) throws SQLException {
        return new MembershipType(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("duration_days"),
                rs.getDouble("price"),
                null
        );
    }
}

