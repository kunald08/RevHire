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
        String sql = "SELECT * FROM applications WHERE user_id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Application a = new Application();
                a.setId(rs.getInt("id"));
                a.setJobId(rs.getInt("job_id"));
                a.setStatus(rs.getString("status"));
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
                "SELECT a.id, a.status, u.name, u.email " +
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
}
