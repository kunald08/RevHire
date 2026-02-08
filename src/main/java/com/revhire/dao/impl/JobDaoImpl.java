package com.revhire.dao.impl;

import com.revhire.dao.JobDao;
import com.revhire.model.Job;
import com.revhire.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDaoImpl implements JobDao {

    // ================= VIEW ALL JOBS =================
    @Override
    public List<Job> getAllJobs() {
        return searchJobs(null, null, null, null);
    }

    // ================= SEARCH JOBS WITH FILTERS =================
    @Override
    public List<Job> searchJobs(String title,
                                String location,
                                String jobType,
                                Integer minExperience) {

        List<Job> jobs = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM jobs WHERE 1=1"
        );

        if (title != null && !title.isBlank()) {
            sql.append(" AND title LIKE ?");
        }
        if (location != null && !location.isBlank()) {
            sql.append(" AND location LIKE ?");
        }
        if (jobType != null && !jobType.isBlank()) {
            sql.append(" AND job_type = ?");
        }
        if (minExperience != null) {
            sql.append(" AND experience >= ?");
        }

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int index = 1;

            if (title != null && !title.isBlank()) {
                ps.setString(index++, "%" + title + "%");
            }
            if (location != null && !location.isBlank()) {
                ps.setString(index++, "%" + location + "%");
            }
            if (jobType != null && !jobType.isBlank()) {
                ps.setString(index++, jobType);
            }
            if (minExperience != null) {
                ps.setInt(index, minExperience);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Job j = new Job();
                j.setId(rs.getInt("id"));
                j.setTitle(rs.getString("title"));
                j.setCompany(rs.getString("company"));
                j.setLocation(rs.getString("location"));
                j.setExperience(rs.getInt("experience"));
                j.setSalary(rs.getDouble("salary"));
                j.setJobType(rs.getString("job_type"));
                jobs.add(j);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jobs;
    }
    @Override
    public boolean createJob(Job j) {
        String sql = "INSERT INTO jobs (title, company, location, experience, salary, job_type, employer_id) VALUES (?,?,?,?,?,?,?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, j.getTitle());
            ps.setString(2, j.getCompany());
            ps.setString(3, j.getLocation());
            ps.setInt(4, j.getExperience());
            ps.setDouble(5, j.getSalary());
            ps.setString(6, j.getJobType());
            ps.setInt(7, j.getEmployerId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Job> getJobsByEmployer(int employerId) {

        List<Job> list = new ArrayList<>();
        String sql = "SELECT * FROM jobs WHERE employer_id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Job j = new Job();
                j.setId(rs.getInt("id"));
                j.setTitle(rs.getString("title"));
                j.setCompany(rs.getString("company"));
                j.setLocation(rs.getString("location"));
                j.setExperience(rs.getInt("experience"));
                j.setSalary(rs.getDouble("salary"));
                j.setJobType(rs.getString("job_type"));
                j.setEmployerId(employerId);
                list.add(j);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    @Override
    public boolean updateJob(Job j) {

        String sql = """
        UPDATE jobs
        SET title=?, company=?, location=?, experience=?, salary=?, job_type=?
        WHERE id=? AND employer_id=?
    """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, j.getTitle());
            ps.setString(2, j.getCompany());
            ps.setString(3, j.getLocation());
            ps.setInt(4, j.getExperience());
            ps.setDouble(5, j.getSalary());
            ps.setString(6, j.getJobType());
            ps.setInt(7, j.getId());
            ps.setInt(8, j.getEmployerId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateJobStatus(int jobId, String status) {

        String sql = "UPDATE jobs SET status=? WHERE id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, jobId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }


}
