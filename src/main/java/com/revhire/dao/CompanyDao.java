package com.revhire.dao;

import com.revhire.model.Company;

/**
 * Data access contract for company operations.
 */
public interface CompanyDao {

    boolean createCompany(Company company);

    Company getCompanyByEmployerId(int employerId);

    boolean updateCompany(Company company);
}
