package com.fitnessclub.repository.jdbc;

import com.fitnessclub.db.Database;
import com.fitnessclub.model.Member;
import com.fitnessclub.repository.MemberRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMemberRepository implements MemberRepository {
    private final Database database;

    public JdbcMemberRepository(Database database) {
        this.database = database;
    }

    @Override
    public Member create(Member member) {
        return create(member.getName(), member.getEmail(), member.getPhone(), member.getMembershipTypeId(), member.getMembershipEndDate());
    }

    @Override
    public Member create(String name, String email, String phone, Integer membershipTypeId, LocalDate membershipEndDate) {
        String sql = """
                INSERT INTO members(name, email, phone, membership_type_id, membership_end_date)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id, created_at
                """;
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            if (membershipTypeId == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, membershipTypeId);
            }
            if (membershipEndDate == null) {
                ps.setNull(5, java.sql.Types.DATE);
            } else {
                ps.setObject(5, membershipEndDate);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    OffsetDateTime createdAt = rs.getObject("created_at", OffsetDateTime.class);
                    return new Member(id, name, email, phone, membershipTypeId, membershipEndDate, createdAt.toLocalDateTime());
                }
            }
            throw new SQLException("Insert did not return id");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create member", e);
        }
    }

    @Override
    public Optional<Member> findById(Integer id) {
        String sql = "SELECT * FROM members WHERE id = ?";
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
            throw new IllegalStateException("Failed to fetch member by id", e);
        }
    }

    @Override
    public List<Member> findAll() {
        String sql = "SELECT * FROM members ORDER BY created_at DESC";
        try (Connection conn = database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            List<Member> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch members", e);
        }
    }

    @Override
    public List<Member> findActiveOn(LocalDate date) {
        String sql = """
                SELECT * FROM members
                WHERE membership_end_date IS NOT NULL
                  AND membership_end_date >= ?
                ORDER BY membership_end_date DESC
                """;
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                List<Member> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(map(rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch active members", e);
        }
    }

    @Override
    public Member updateMembership(int memberId, Integer membershipTypeId, LocalDate membershipEndDate) {
        String sql = """
                UPDATE members
                SET membership_type_id = ?, membership_end_date = ?
                WHERE id = ?
                RETURNING *
                """;
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (membershipTypeId == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, membershipTypeId);
            }
            if (membershipEndDate == null) {
                ps.setNull(2, java.sql.Types.DATE);
            } else {
                ps.setObject(2, membershipEndDate);
            }
            ps.setInt(3, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
            throw new SQLException("Member not found");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update membership", e);
        }
    }

    private Member map(ResultSet rs) throws SQLException {
        return new Member(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                (Integer) rs.getObject("membership_type_id"),
                rs.getObject("membership_end_date", LocalDate.class),
                rs.getObject("created_at", OffsetDateTime.class).toLocalDateTime()
        );
    }
}
