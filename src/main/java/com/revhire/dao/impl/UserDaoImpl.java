package com.revhire.dao.impl;

import com.revhire.dao.UserDao;
import com.revhire.model.User;
import com.revhire.util.DBUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    @Override
    public boolean register(User u) {
        String sql = """
            INSERT INTO users (name, email, password, role, phone, education,
                               work_experience, skills, certifications,
                               security_question, security_answer)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getRole());
            ps.setString(5, u.getPhone());
            ps.setString(6, u.getEducation());
            ps.setString(7, u.getWorkExperience());
            ps.setString(8, u.getSkills());
            ps.setString(9, u.getCertifications());
            ps.setString(10, u.getSecurityQuestion());
            ps.setString(11, u.getSecurityAnswer());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Register failed for email={}: {}", u.getEmail(), e.getMessage());
            return false;
        }
    }

    @Override
    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapUser(rs);

        } catch (SQLException e) {
            logger.error("Login query failed: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapUser(rs);

        } catch (SQLException e) {
            logger.error("Get user by id failed userId={}: {}", userId, e.getMessage());
        }
        return null;
    }

    @Override
    public boolean updateProfile(User u) {
        String sql = """
            UPDATE users SET name = ?, phone = ?, education = ?,
                             work_experience = ?, skills = ?, certifications = ?
            WHERE id = ?
            """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getName());
            ps.setString(2, u.getPhone());
            ps.setString(3, u.getEducation());
            ps.setString(4, u.getWorkExperience());
            ps.setString(5, u.getSkills());
            ps.setString(6, u.getCertifications());
            ps.setInt(7, u.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Update profile failed userId={}: {}", u.getId(), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean changePassword(int userId, String oldPwd, String newPwd) {
        String sql = "UPDATE users SET password = ? WHERE id = ? AND password = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPwd);
            ps.setInt(2, userId);
            ps.setString(3, oldPwd);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Change password failed userId={}: {}", userId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean resetPassword(String email, String question, String answer, String newPassword) {
        String sql = """
            UPDATE users SET password = ?
            WHERE email = ? AND security_question = ? AND security_answer = ?
            """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, email);
            ps.setString(3, question);
            ps.setString(4, answer);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Reset password failed email={}: {}", email, e.getMessage());
            return false;
        }
    }

    // ── Helper ──

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setPhone(rs.getString("phone"));
        u.setEducation(rs.getString("education"));
        u.setWorkExperience(rs.getString("work_experience"));
        u.setSkills(rs.getString("skills"));
        u.setCertifications(rs.getString("certifications"));
        u.setSecurityQuestion(rs.getString("security_question"));
        u.setSecurityAnswer(rs.getString("security_answer"));
        return u;
    }
}
