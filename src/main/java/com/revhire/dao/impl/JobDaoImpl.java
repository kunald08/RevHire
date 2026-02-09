package com.revhire.dao.impl;

import com.revhire.dao.JobDao;
import com.revhire.model.Job;
import com.revhire.util.DBUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDaoImpl implements JobDao {

    private static final Logger logger = LogManager.getLogger(JobDaoImpl.class);

    @Override
    public List<Job> getAllJobs() {
        return searchJobs(null, null, null, null, null, null, null);
    }

    @Override
    public List<Job> searchJobs(String title, String location, String jobType,
                                Integer minExperience, String company,
                                Double salaryMin, Double salaryMax) {
        List<Job> jobs = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM jobs WHERE status = 'OPEN'");

        if (title != null && !title.isBlank())       sql.append(" AND title LIKE ?");
        if (location != null && !location.isBlank()) sql.append(" AND location LIKE ?");
        if (jobType != null && !jobType.isBlank())   sql.append(" AND job_type = ?");
        if (minExperience != null)                   sql.append(" AND experience >= ?");
        if (company != null && !company.isBlank())   sql.append(" AND company LIKE ?");
        if (salaryMin != null)                       sql.append(" AND salary_max >= ?");
        if (salaryMax != null)                       sql.append(" AND salary_min <= ?");

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;
            if (title != null && !title.isBlank())       ps.setString(idx++, "%" + title + "%");
            if (location != null && !location.isBlank()) ps.setString(idx++, "%" + location + "%");
            if (jobType != null && !jobType.isBlank())   ps.setString(idx++, jobType);
            if (minExperience != null)                   ps.setInt(idx++, minExperience);
            if (company != null && !company.isBlank())   ps.setString(idx++, "%" + company + "%");
            if (salaryMin != null)                       ps.setDouble(idx++, salaryMin);
            if (salaryMax != null)                       ps.setDouble(idx++, salaryMax);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                jobs.add(mapJob(rs));
            }

        } catch (SQLException e) {
            logger.error("Search jobs failed: {}", e.getMessage());
        }
        return jobs;
    }

    @Override
    public Job getJobById(int jobId) {
        String sql = "SELECT * FROM jobs WHERE id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapJob(rs);

        } catch (SQLException e) {
            logger.error("Get job by id failed jobId={}: {}", jobId, e.getMessage());
        }
        return null;
    }

    @Override
    public boolean createJob(Job j) {
        String sql = """
            INSERT INTO jobs (employer_id, title, description, company, location,
                              skills_required, experience, education_req,
                              salary_min, salary_max, job_type, deadline)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, j.getEmployerId());
            ps.setString(2, j.getTitle());
            ps.setString(3, j.getDescription());
            ps.setString(4, j.getCompany());
            ps.setString(5, j.getLocation());
            ps.setString(6, j.getSkillsRequired());
            ps.setInt(7, j.getExperience());
            ps.setString(8, j.getEducationReq());
            ps.setDouble(9, j.getSalaryMin());
            ps.setDouble(10, j.getSalaryMax());
            ps.setString(11, j.getJobType());
            if (j.getDeadline() != null && !j.getDeadline().isBlank()) {
                ps.setString(12, j.getDeadline());
            } else {
                ps.setNull(12, Types.DATE);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Create job failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<Job> getJobsByEmployer(int employerId) {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT * FROM jobs WHERE employer_id = ? ORDER BY created_at DESC";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapJob(rs));
            }

        } catch (SQLException e) {
            logger.error("Get jobs by employer failed: {}", e.getMessage());
        }
        return list;
    }

    @Override
    public boolean updateJob(Job j) {
        String sql = """
            UPDATE jobs
            SET title = ?, description = ?, company = ?, location = ?,
                skills_required = ?, experience = ?, education_req = ?,
                salary_min = ?, salary_max = ?, job_type = ?, deadline = ?
            WHERE id = ? AND employer_id = ?
            """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, j.getTitle());
            ps.setString(2, j.getDescription());
            ps.setString(3, j.getCompany());
            ps.setString(4, j.getLocation());
            ps.setString(5, j.getSkillsRequired());
            ps.setInt(6, j.getExperience());
            ps.setString(7, j.getEducationReq());
            ps.setDouble(8, j.getSalaryMin());
            ps.setDouble(9, j.getSalaryMax());
            ps.setString(10, j.getJobType());
            if (j.getDeadline() != null && !j.getDeadline().isBlank()) {
                ps.setString(11, j.getDeadline());
            } else {
                ps.setNull(11, Types.DATE);
            }
            ps.setInt(12, j.getId());
            ps.setInt(13, j.getEmployerId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Update job failed jobId={}: {}", j.getId(), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateJobStatus(int jobId, String status) {
        String sql = "UPDATE jobs SET status = ? WHERE id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, jobId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Update job status failed jobId={}: {}", jobId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteJob(int jobId, int employerId) {
        // First delete applications for this job, then the job itself
        String delApps = "DELETE FROM applications WHERE job_id = ?";
        String delJob  = "DELETE FROM jobs WHERE id = ? AND employer_id = ?";

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps1 = con.prepareStatement(delApps)) {
                ps1.setInt(1, jobId);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = con.prepareStatement(delJob)) {
                ps2.setInt(1, jobId);
                ps2.setInt(2, employerId);
                int rows = ps2.executeUpdate();
                con.commit();
                return rows > 0;
            }

        } catch (SQLException e) {
            logger.error("Delete job failed jobId={}: {}", jobId, e.getMessage());
            return false;
        }
    }

    // ── Helper ──

    private Job mapJob(ResultSet rs) throws SQLException {
        Job j = new Job();
        j.setId(rs.getInt("id"));
        j.setEmployerId(rs.getInt("employer_id"));
        j.setTitle(rs.getString("title"));
        j.setDescription(rs.getString("description"));
        j.setCompany(rs.getString("company"));
        j.setLocation(rs.getString("location"));
        j.setSkillsRequired(rs.getString("skills_required"));
        j.setExperience(rs.getInt("experience"));
        j.setEducationReq(rs.getString("education_req"));
        j.setSalaryMin(rs.getDouble("salary_min"));
        j.setSalaryMax(rs.getDouble("salary_max"));
        j.setJobType(rs.getString("job_type"));
        j.setStatus(rs.getString("status"));
        Date d = rs.getDate("deadline");
        j.setDeadline(d != null ? d.toString() : null);
        return j;
    }
}
