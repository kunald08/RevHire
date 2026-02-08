package com.revhire.dao.impl;

import com.revhire.dao.ApplicationDao;
import com.revhire.model.Application;
import com.revhire.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDaoImpl implements ApplicationDao {

    public boolean applyJob(int userId, int jobId) {
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
