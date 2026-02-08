package com.revhire.service;

import com.revhire.dao.impl.*;
import com.revhire.model.*;

import java.util.List;

public class JobSeekerService {

    private JobDaoImpl jobDao = new JobDaoImpl();
    private ResumeDaoImpl resumeDao = new ResumeDaoImpl();
    private ApplicationDaoImpl appDao = new ApplicationDaoImpl();

    public void viewAllJobs() {
        printJobs(jobDao.getAllJobs());
    }

    public void searchJobs(String title, String location, String type, Integer exp) {
        printJobs(jobDao.searchJobs(title, location, type, exp));
    }

    private void printJobs(List<Job> jobs) {
        if (jobs.isEmpty()) {
            System.out.println("❌ No jobs found");
            return;
        }
        for (Job j : jobs) {
            System.out.println(
                    j.getId() + " | " +
                            j.getTitle() + " | " +
                            j.getCompany() + " | " +
                            j.getLocation() + " | " +
                            j.getJobType() + " | Exp: " +
                            j.getExperience()
            );
        }
    }

    public boolean createResume(Resume r) {
        return resumeDao.saveResume(r);
    }

    public boolean applyJob(int userId, int jobId) {

        if (resumeDao.getResumeByUserId(userId) == null) {
            System.out.println("❌ Create resume before applying");
            return false;
        }
        return appDao.applyJob(userId, jobId);
    }

    public void viewApplications(int userId) {
        appDao.getApplicationsByUser(userId)
                .forEach(a ->
                        System.out.println("AppID: " + a.getId() +
                                " JobID: " + a.getJobId() +
                                " Status: " + a.getStatus())
                );
    }

    public boolean withdraw(int appId) {
        return appDao.withdrawApplication(appId);
    }
}
