package com.revhire.dao.impl;

import com.revhire.dao.ResumeDao;
import com.revhire.model.Resume;
import com.revhire.util.DBUtil;

import java.sql.*;

public class ResumeDaoImpl implements ResumeDao {

    public boolean saveResume(Resume r) {
        String sql = "INSERT INTO resumes (user_id, objective, skills, experience, education) VALUES (?,?,?,?,?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, r.getUserId());
            ps.setString(2, r.getObjective());
            ps.setString(3, r.getSkills());
            ps.setString(4, r.getExperience());
            ps.setString(5, r.getEducation());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public Resume getResumeByUserId(int userId) {
        String sql = "SELECT * FROM resumes WHERE user_id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Resume r = new Resume();
                r.setUserId(userId);
                r.setObjective(rs.getString("objective"));
                r.setSkills(rs.getString("skills"));
                r.setExperience(rs.getString("experience"));
                r.setEducation(rs.getString("education"));
                return r;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
