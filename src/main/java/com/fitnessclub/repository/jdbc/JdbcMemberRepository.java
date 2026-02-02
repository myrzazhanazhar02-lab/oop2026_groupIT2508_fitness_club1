package com.fitnessclub.repository.jdbc;

import com.fitnessclub.db.Database;
import com.fitnessclub.model.Member;
import com.fitnessclub.repository.MemberRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMemberRepository implements MemberRepository {

    private final Database database;

    public JdbcMemberRepository(Database database) {
        this.database = database;
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
            if (membershipTypeId == null) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, membershipTypeId);

            if (membershipEndDate == null) ps.setNull(5, Types.DATE);
            else ps.setDate(5, Date.valueOf(membershipEndDate));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // IMPORTANT: подстрой под свой конструктор Member (поля могут отличаться)
                    return new Member(
                            rs.getInt("id"),
                            name,
                            email,
                            phone,
                            membershipTypeId,
                            membershipEndDate,
                            rs.getObject("created_at", Timestamp.class).toLocalDateTime()
                    );
                }
                throw new SQLException("Insert failed");
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create member", e);
        }
    }

    // ✅ ОДИН findById — и он должен быть (Integer id)
    @Override
    public Optional<Member> findById(Integer id) {
        String sql = "SELECT * FROM members WHERE id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }

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
            while (rs.next()) result.add(map(rs));
            return result;

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch members", e);
        }
    }

    @Override
    public void save(Member entity) {
        throw new UnsupportedOperationException("save() not implemented yet");
    }

    @Override
    public void update(Member entity) {
        throw new UnsupportedOperationException("update() not implemented yet");
    }

    @Override
    public void deleteById(Integer id) {
        throw new UnsupportedOperationException("deleteById() not implemented yet");
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

            if (membershipTypeId == null) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, membershipTypeId);

            if (membershipEndDate == null) ps.setNull(2, Types.DATE);
            else ps.setDate(2, Date.valueOf(membershipEndDate));

            ps.setInt(3, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                throw new SQLException("Update failed");
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update membership", e);
        }
    }

    private Member map(ResultSet rs) throws SQLException {
        Integer membershipTypeId = (Integer) rs.getObject("membership_type_id");
        Date endDate = rs.getDate("membership_end_date");

        // IMPORTANT: подстрой под свой конструктор Member
        return new Member(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                membershipTypeId,
                endDate == null ? null : endDate.toLocalDate(),
                rs.getObject("created_at", Timestamp.class).toLocalDateTime()
        );
    }
}
