package com.revhire.dao.impl;

import com.revhire.dao.ApplicationDao;
import com.revhire.model.Application;
import com.revhire.util.DBUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDaoImpl implements ApplicationDao {

    private static final Logger logger = LogManager.getLogger(ApplicationDaoImpl.class);

    @Override
    public boolean applyJob(int userId, int jobId, String coverLetter) {
        String sql = "INSERT INTO applications (user_id, job_id, cover_letter, status) VALUES (?, ?, ?, 'APPLIED')";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, jobId);
            ps.setString(3, coverLetter);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Apply job failed userId={} jobId={}: {}", userId, jobId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean alreadyApplied(int userId, int jobId) {
        String sql = "SELECT COUNT(*) FROM applications WHERE user_id = ? AND job_id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, jobId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            logger.error("Already-applied check failed: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public List<Application> getApplicationsByUser(int userId) {
        List<Application> list = new ArrayList<>();
        String sql = """
            SELECT a.id, a.job_id, a.cover_letter, a.status, a.comment,
                   a.withdraw_reason, a.applied_at,
                   j.title AS job_title, j.company AS job_company
            FROM applications a
            JOIN jobs j ON a.job_id = j.id
            WHERE a.user_id = ?
            ORDER BY a.applied_at DESC
            """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Application a = new Application();
                a.setId(rs.getInt("id"));
                a.setJobId(rs.getInt("job_id"));
                a.setCoverLetter(rs.getString("cover_letter"));
                a.setStatus(rs.getString("status"));
                a.setComment(rs.getString("comment"));
                a.setWithdrawReason(rs.getString("withdraw_reason"));
                Timestamp ts = rs.getTimestamp("applied_at");
                a.setAppliedAt(ts != null ? ts.toString().substring(0, 16) : null);
                a.setJobTitle(rs.getString("job_title"));
                a.setJobCompany(rs.getString("job_company"));
                list.add(a);
            }

        } catch (SQLException e) {
            logger.error("Get applications by user failed: {}", e.getMessage());
        }
        return list;
    }

    @Override
    public List<Application> getApplicationsByJob(int jobId) {
        List<Application> list = new ArrayList<>();

        String sql = """
            SELECT a.id, a.user_id, a.status, a.comment, a.cover_letter, a.applied_at,
                   u.name, u.email
            FROM applications a
            JOIN users u ON a.user_id = u.id
            WHERE a.job_id = ?
            ORDER BY a.applied_at DESC
            """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Application a = new Application();
                a.setId(rs.getInt("id"));
                a.setUserId(rs.getInt("user_id"));
                a.setStatus(rs.getString("status"));
                a.setComment(rs.getString("comment"));
                a.setCoverLetter(rs.getString("cover_letter"));
                Timestamp ts = rs.getTimestamp("applied_at");
                a.setAppliedAt(ts != null ? ts.toString().substring(0, 16) : null);
                a.setApplicantName(rs.getString("name"));
                a.setApplicantEmail(rs.getString("email"));
                list.add(a);
            }

        } catch (SQLException e) {
            logger.error("Get applications by job failed: {}", e.getMessage());
        }
        return list;
    }

    @Override
    public boolean updateStatus(int applicationId, String status) {
        String sql = "UPDATE applications SET status = ? WHERE id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, applicationId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Update status failed appId={}: {}", applicationId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateStatusBulk(List<Integer> applicationIds, String status) {
        if (applicationIds == null || applicationIds.isEmpty()) return false;

        StringBuilder sql = new StringBuilder("UPDATE applications SET status = ? WHERE id IN (");
        for (int i = 0; i < applicationIds.size(); i++) {
            sql.append(i > 0 ? ",?" : "?");
        }
        sql.append(")");

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            ps.setString(1, status);
            int idx = 2;
            for (int id : applicationIds) {
                ps.setInt(idx++, id);
            }
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Bulk status update failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean addComment(int applicationId, String comment) {
        String sql = "UPDATE applications SET comment = ? WHERE id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, comment);
            ps.setInt(2, applicationId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Add comment failed appId={}: {}", applicationId, e.getMessage());
            return false;
        }
    }

    @Override
    public int countByJobAndStatus(int jobId, String status) {
        String sql = "SELECT COUNT(*) FROM applications WHERE job_id = ? AND status = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ps.setString(2, status);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            logger.error("Count by status failed: {}", e.getMessage());
        }
        return 0;
    }

    @Override
    public int getUserIdByApplication(int applicationId) {
        String sql = "SELECT user_id FROM applications WHERE id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, applicationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("user_id");

        } catch (SQLException e) {
            logger.error("Get userId by appId failed: {}", e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean withdrawApplication(int appId, String reason) {
        String sql = "UPDATE applications SET status = 'WITHDRAWN', withdraw_reason = ? WHERE id = ? AND status = 'APPLIED'";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, reason);
            ps.setInt(2, appId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Withdraw application failed appId={}: {}", appId, e.getMessage());
            return false;
        }
    }

    @Override
    public List<Application> filterApplicants(int jobId, String skill, Integer experience,
                                              String education, String dateFrom) {
        List<Application> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT a.id, a.status, a.applied_at, u.name, u.email
            FROM applications a
            JOIN users u ON a.user_id = u.id
            LEFT JOIN resumes r ON u.id = r.user_id
            WHERE a.job_id = ?
            """);

        if (skill != null && !skill.isBlank())       sql.append(" AND r.skills LIKE ?");
        if (education != null && !education.isBlank()) sql.append(" AND r.education LIKE ?");
        if (dateFrom != null && !dateFrom.isBlank()) sql.append(" AND a.applied_at >= ?");

        sql.append(" ORDER BY a.applied_at DESC");

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;
            ps.setInt(idx++, jobId);
            if (skill != null && !skill.isBlank())       ps.setString(idx++, "%" + skill + "%");
            if (education != null && !education.isBlank()) ps.setString(idx++, "%" + education + "%");
            if (dateFrom != null && !dateFrom.isBlank()) ps.setString(idx++, dateFrom);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Application a = new Application();
                a.setId(rs.getInt("id"));
                a.setStatus(rs.getString("status"));
                Timestamp ts = rs.getTimestamp("applied_at");
                a.setAppliedAt(ts != null ? ts.toString().substring(0, 16) : null);
                a.setApplicantName(rs.getString("name"));
                a.setApplicantEmail(rs.getString("email"));
                list.add(a);
            }

        } catch (SQLException e) {
            logger.error("Filter applicants failed: {}", e.getMessage());
        }
        return list;
    }
}
