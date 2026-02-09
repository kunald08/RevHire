package com.revhire.dao.impl;

import com.revhire.dao.ResumeDao;
import com.revhire.model.Resume;
import com.revhire.util.DBUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class ResumeDaoImpl implements ResumeDao {

    private static final Logger logger = LogManager.getLogger(ResumeDaoImpl.class);

    @Override
    public boolean saveResume(Resume r) {
        String sql = """
            INSERT INTO resumes (user_id, objective, education, experience, skills, projects)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, r.getUserId());
            ps.setString(2, r.getObjective());
            ps.setString(3, r.getEducation());
            ps.setString(4, r.getExperience());
            ps.setString(5, r.getSkills());
            ps.setString(6, r.getProjects());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Save resume failed userId={}: {}", r.getUserId(), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateResume(Resume r) {
        String sql = """
            UPDATE resumes SET objective = ?, education = ?, experience = ?,
                               skills = ?, projects = ?
            WHERE user_id = ?
            """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, r.getObjective());
            ps.setString(2, r.getEducation());
            ps.setString(3, r.getExperience());
            ps.setString(4, r.getSkills());
            ps.setString(5, r.getProjects());
            ps.setInt(6, r.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Update resume failed userId={}: {}", r.getUserId(), e.getMessage());
            return false;
        }
    }

    @Override
    public Resume getResumeByUserId(int userId) {
        String sql = "SELECT * FROM resumes WHERE user_id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapResume(rs);

        } catch (SQLException e) {
            logger.error("Get resume by userId failed: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public Resume getResumeByApplicationId(int appId) {
        String sql = """
            SELECT r.* FROM resumes r
            JOIN applications a ON r.user_id = a.user_id
            WHERE a.id = ?
            """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, appId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapResume(rs);

        } catch (SQLException e) {
            logger.error("Get resume by appId failed: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public boolean hasResume(int userId) {
        String sql = "SELECT COUNT(*) FROM resumes WHERE user_id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            logger.error("Has-resume check failed userId={}: {}", userId, e.getMessage());
        }
        return false;
    }

    // ── Helper ──

    private Resume mapResume(ResultSet rs) throws SQLException {
        Resume r = new Resume();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setObjective(rs.getString("objective"));
        r.setEducation(rs.getString("education"));
        r.setExperience(rs.getString("experience"));
        r.setSkills(rs.getString("skills"));
        r.setProjects(rs.getString("projects"));
        return r;
    }
}
