package com.revhire.dao;

import com.revhire.model.Company;

public interface CompanyDao {

    boolean createCompany(Company company);

    Company getCompanyByEmployerId(int employerId);
}
