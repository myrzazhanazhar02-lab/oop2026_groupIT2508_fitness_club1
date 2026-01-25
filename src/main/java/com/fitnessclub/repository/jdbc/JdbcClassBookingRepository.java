package com.fitnessclub.repository.jdbc;

import com.fitnessclub.db.Database;
import com.fitnessclub.model.ClassBooking;
import com.fitnessclub.repository.ClassBookingRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcClassBookingRepository implements ClassBookingRepository {
    private final Database database;

    public JdbcClassBookingRepository(Database database) {
        this.database = database;
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
                    return new ClassBooking(
                            rs.getInt("id"),
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
                return 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to count bookings", e);
        }
    }

    @Override
    public List<ClassBooking> findByMember(int memberId) {
        String sql = "SELECT * FROM class_bookings WHERE member_id = ? ORDER BY booked_at DESC";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                List<ClassBooking> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(new ClassBooking(
                            rs.getInt("id"),
                            rs.getInt("member_id"),
                            rs.getInt("class_id"),
                            rs.getObject("booked_at", OffsetDateTime.class).toLocalDateTime()
                    ));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch bookings", e);
        }
    }
}
