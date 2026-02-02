package com.fitnessclub.repository.jdbc;

import com.fitnessclub.db.Database;
import com.fitnessclub.model.ClassBooking;
import com.fitnessclub.repository.ClassBookingRepository;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcClassBookingRepository implements ClassBookingRepository {

    private final Database database;

    public JdbcClassBookingRepository(Database database) {
        this.database = database;
    }

    @Override
    public Optional<ClassBooking> findById(Integer id) {
        String sql = "SELECT id, member_id, class_id, booked_at FROM class_bookings WHERE id = ?";
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
            throw new IllegalStateException("Failed to fetch booking by id", e);
        }
    }

    @Override
    public List<ClassBooking> findAll() {
        String sql = "SELECT id, member_id, class_id, booked_at FROM class_bookings ORDER BY booked_at DESC";
        try (Connection conn = database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            List<ClassBooking> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch bookings", e);
        }
    }

    @Override
    public void save(ClassBooking entity) {
        String sql = "INSERT INTO class_bookings(member_id, class_id) VALUES (?, ?)";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entity.getMemberId());
            ps.setInt(2, entity.getClassId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save booking", e);
        }
    }

    @Override
    public void update(ClassBooking entity) {
        String sql = "UPDATE class_bookings SET member_id = ?, class_id = ? WHERE id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entity.getMemberId());
            ps.setInt(2, entity.getClassId());
            ps.setInt(3, entity.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update booking", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM class_bookings WHERE id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete booking", e);
        }
    }

    @Override
    public ClassBooking create(int memberId, int classId) {
        String sql = """
                INSERT INTO class_bookings(member_id, class_id)
                VALUES (?, ?)
                RETURNING id, booked_at
                """;
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            ps.setInt(2, classId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    return new ClassBooking(
                            id,
                            memberId,
                            classId,
                            rs.getObject("booked_at", OffsetDateTime.class).toLocalDateTime()
                    );
                }
            }

            throw new SQLException("Insert failed");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create booking", e);
        }
    }

    @Override
    public boolean existsByMemberAndClass(int memberId, int classId) {
        String sql = "SELECT 1 FROM class_bookings WHERE member_id = ? AND class_id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            ps.setInt(2, classId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to check booking existence", e);
        }
    }

    @Override
    public int countBookingsForClass(int classId) {
        String sql = "SELECT COUNT(*) FROM class_bookings WHERE class_id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, classId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            return 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to count bookings", e);
        }
    }

    @Override
    public List<ClassBooking> findByMember(int memberId) {
        String sql = "SELECT id, member_id, class_id, booked_at FROM class_bookings WHERE member_id = ? ORDER BY booked_at DESC";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                List<ClassBooking> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(map(rs));
                }
                return result;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch bookings by member", e);
        }
    }

    private ClassBooking map(ResultSet rs) throws SQLException {
        return new ClassBooking(
                rs.getInt("id"),
                rs.getInt("member_id"),
                rs.getInt("class_id"),
                rs.getObject("booked_at", OffsetDateTime.class).toLocalDateTime()
        );
    }
}
