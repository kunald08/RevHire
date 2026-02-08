package com.revhire.service;

import com.revhire.dao.impl.ApplicationDaoImpl;
import com.revhire.dao.impl.CompanyDaoImpl;
import com.revhire.dao.impl.JobDaoImpl;
import com.revhire.model.Application;
import com.revhire.model.Company;
import com.revhire.model.Job;

import java.util.List;

public class EmployerService {

    private CompanyDaoImpl companyDao = new CompanyDaoImpl();
    private JobDaoImpl jobDao = new JobDaoImpl();
    private ApplicationDaoImpl appDao = new ApplicationDaoImpl();

    public boolean createCompany(Company c) {
        return companyDao.createCompany(c);
    }

    public boolean postJob(Job j) {
        return jobDao.createJob(j);
    }

    public void viewMyJobs(int employerId) {
        jobDao.getJobsByEmployer(employerId)
                .forEach(j ->
                        System.out.println(j.getId() + " | " + j.getTitle() + " | " + j.getLocation())
                );
    }

    // 🔥 NEW
    public void viewApplicants(int jobId) {
        List<Application> apps = appDao.getApplicationsByJob(jobId);

        if (apps.isEmpty()) {
            System.out.println("No applicants yet");
            return;
        }

        for (Application a : apps) {
            System.out.println(
                    "AppID: " + a.getId() +
                            " | Name: " + a.getApplicantName() +
                            " | Email: " + a.getApplicantEmail() +
                            " | Status: " + a.getStatus()
            );
        }
    }

    public boolean updateApplicationStatus(int appId, String status) {
        return appDao.updateStatus(appId, status);
    }
}
