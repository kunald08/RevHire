package com.revhire.dao.impl;

import com.revhire.dao.JobDao;
import com.revhire.model.Job;
import com.revhire.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDaoImpl implements JobDao {

    public List<Job> getAllJobs() {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM jobs";

        try (Connection con = DBUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobs;
    }
}
