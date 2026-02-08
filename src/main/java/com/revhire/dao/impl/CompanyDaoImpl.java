package com.revhire.dao.impl;

import com.revhire.dao.CompanyDao;
import com.revhire.model.Company;
import com.revhire.util.DBUtil;

import java.sql.*;

public class CompanyDaoImpl implements CompanyDao {

    @Override
    public boolean createCompany(Company c) {
        String sql = "INSERT INTO companies (employer_id, name, industry, location, description) VALUES (?,?,?,?,?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, c.getEmployerId());
            ps.setString(2, c.getName());
            ps.setString(3, c.getIndustry());
            ps.setString(4, c.getLocation());
            ps.setString(5, c.getDescription());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Company getCompanyByEmployerId(int employerId) {
        String sql = "SELECT * FROM companies WHERE employer_id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Company c = new Company();
                c.setId(rs.getInt("id"));
                c.setEmployerId(employerId);
                c.setName(rs.getString("name"));
                c.setIndustry(rs.getString("industry"));
                c.setLocation(rs.getString("location"));
                c.setDescription(rs.getString("description"));
                return c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
