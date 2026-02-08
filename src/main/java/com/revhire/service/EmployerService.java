package com.revhire.service;

import com.revhire.dao.impl.*;
import com.revhire.model.*;

import java.util.List;

public class EmployerService {

    private CompanyDaoImpl companyDao = new CompanyDaoImpl();
    private JobDaoImpl jobDao = new JobDaoImpl();
    private ApplicationDaoImpl appDao = new ApplicationDaoImpl();

    public boolean createCompany(Company c) {
        return companyDao.createCompany(c);
    }

    public Company getCompany(int employerId) {
        return companyDao.getCompanyByEmployerId(employerId);
    }

    public boolean postJob(Job j) {
        return jobDao.createJob(j);
    }

    public void viewMyJobs(int employerId) {
        List<Job> jobs = jobDao.getJobsByEmployer(employerId);
        jobs.forEach(j ->
                System.out.println(j.getId() + " | " + j.getTitle() + " | " + j.getLocation())
        );
    }

    public void viewApplicants(int jobId) {
        appDao.getApplicationsByUser(jobId); // placeholder (we’ll improve)
    }
}
