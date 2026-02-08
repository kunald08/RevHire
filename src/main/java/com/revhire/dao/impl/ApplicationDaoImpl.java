package com.revhire.dao.impl;

import com.revhire.dao.ApplicationDao;
import com.revhire.model.Application;
import com.revhire.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDaoImpl implements ApplicationDao {

    @Override
    public boolean alreadyApplied(int userId, int jobId) {
        String sql = "SELECT id FROM applications WHERE user_id=? AND job_id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, jobId);
            return ps.executeQuery().next();

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean applyJob(int userId, int jobId) {
        if (alreadyApplied(userId, jobId)) return false;

        String sql = "INSERT INTO applications (user_id, job_id) VALUES (?,?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, jobId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Application> getApplicationsByUser(int userId) {

        List<Application> list = new ArrayList<>();

        String sql =
                "SELECT id, job_id, status, comment " +
                        "FROM applications WHERE user_id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Application a = new Application();
                a.setId(rs.getInt("id"));
                a.setJobId(rs.getInt("job_id"));
                a.setStatus(rs.getString("status"));
                a.setComment(rs.getString("comment")); // ✅ NOW POPULATED
                list.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    // ================= EMPLOYER VIEW =================
    @Override
    public List<Application> getApplicationsByJob(int jobId) {

        List<Application> list = new ArrayList<>();

        String sql =
                "SELECT a.id, a.status, a.comment, u.name, u.email " +
                        "FROM applications a " +
                        "JOIN users u ON a.user_id = u.id " +
                        "WHERE a.job_id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Application a = new Application();
                a.setId(rs.getInt("id"));
                a.setStatus(rs.getString("status"));
                a.setApplicantName(rs.getString("name"));
                a.setApplicantEmail(rs.getString("email"));
                a.setComment(rs.getString("comment")); // ✅ NOW VALID
                list.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean updateStatus(int applicationId, String status) {

        String sql = "UPDATE applications SET status=? WHERE id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, applicationId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean withdrawApplication(int appId) {
        String sql = "DELETE FROM applications WHERE id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, appId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int countByJobAndStatus(int jobId, String status) {

        String sql = "SELECT COUNT(*) FROM applications WHERE job_id=? AND status=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ps.setString(2, status);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Application> filterApplicants(
            int jobId,
            String skill,
            Integer minExperience,
            String education) {

        List<Application> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT a.id, a.status, u.name, u.email " +
                        "FROM applications a " +
                        "JOIN users u ON a.user_id = u.id " +
                        "JOIN resumes r ON r.user_id = u.id " +
                        "WHERE a.job_id = ?"
        );

        if (skill != null && !skill.isBlank()) {
            sql.append(" AND r.skills LIKE ?");
        }
        if (minExperience != null) {
            sql.append(" AND r.experience LIKE ?");
        }
        if (education != null && !education.isBlank()) {
            sql.append(" AND r.education LIKE ?");
        }

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;
            ps.setInt(idx++, jobId);

            if (skill != null && !skill.isBlank()) {
                ps.setString(idx++, "%" + skill + "%");
            }
            if (minExperience != null) {
                ps.setString(idx++, "%" + minExperience + "%");
            }
            if (education != null && !education.isBlank()) {
                ps.setString(idx++, "%" + education + "%");
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Application a = new Application();
                a.setId(rs.getInt("id"));
                a.setStatus(rs.getString("status"));
                a.setApplicantName(rs.getString("name"));
                a.setApplicantEmail(rs.getString("email"));
                list.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public boolean updateStatusBulk(List<Integer> applicationIds, String status) {

        if (applicationIds == null || applicationIds.isEmpty()) {
            return false;
        }

        StringBuilder sql = new StringBuilder(
                "UPDATE applications SET status=? WHERE id IN ("
        );

        for (int i = 0; i < applicationIds.size(); i++) {
            sql.append("?");
            if (i < applicationIds.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(")");

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            ps.setString(1, status);

            int idx = 2;
            for (Integer id : applicationIds) {
                ps.setInt(idx++, id);
            }

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addComment(int applicationId, String comment) {

        String sql = "UPDATE applications SET comment=? WHERE id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, comment);
            ps.setInt(2, applicationId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int getUserIdByApplication(int applicationId) {

        String sql = "SELECT user_id FROM applications WHERE id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, applicationId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }



}
