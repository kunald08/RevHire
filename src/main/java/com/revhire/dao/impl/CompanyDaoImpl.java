package com.revhire.dao.impl;

import com.revhire.dao.CompanyDao;
import com.revhire.model.Company;
import com.revhire.util.DBUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class CompanyDaoImpl implements CompanyDao {

    private static final Logger logger = LogManager.getLogger(CompanyDaoImpl.class);

    @Override
    public boolean createCompany(Company c) {
        String sql = """
            INSERT INTO companies (employer_id, name, industry, size, description, website, location)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, c.getEmployerId());
            ps.setString(2, c.getName());
            ps.setString(3, c.getIndustry());
            ps.setString(4, c.getSize());
            ps.setString(5, c.getDescription());
            ps.setString(6, c.getWebsite());
            ps.setString(7, c.getLocation());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Create company failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Company getCompanyByEmployerId(int employerId) {
        String sql = "SELECT * FROM companies WHERE employer_id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapCompany(rs);

        } catch (SQLException e) {
            logger.error("Get company failed employerId={}: {}", employerId, e.getMessage());
        }
        return null;
    }

    @Override
    public boolean updateCompany(Company c) {
        String sql = """
            UPDATE companies SET name = ?, industry = ?, size = ?,
                                 description = ?, website = ?, location = ?
            WHERE employer_id = ?
            """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getName());
            ps.setString(2, c.getIndustry());
            ps.setString(3, c.getSize());
            ps.setString(4, c.getDescription());
            ps.setString(5, c.getWebsite());
            ps.setString(6, c.getLocation());
            ps.setInt(7, c.getEmployerId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Update company failed employerId={}: {}", c.getEmployerId(), e.getMessage());
            return false;
        }
    }

    // ── Helper ──

    private Company mapCompany(ResultSet rs) throws SQLException {
        Company c = new Company();
        c.setId(rs.getInt("id"));
        c.setEmployerId(rs.getInt("employer_id"));
        c.setName(rs.getString("name"));
        c.setIndustry(rs.getString("industry"));
        c.setSize(rs.getString("size"));
        c.setDescription(rs.getString("description"));
        c.setWebsite(rs.getString("website"));
        c.setLocation(rs.getString("location"));
        return c;
    }
}
