package com.revhire.service;

import com.revhire.dao.impl.*;
import com.revhire.model.*;

import java.util.List;

public class JobSeekerService {

    private JobDaoImpl jobDao = new JobDaoImpl();
    private ResumeDaoImpl resumeDao = new ResumeDaoImpl();
    private ApplicationDaoImpl appDao = new ApplicationDaoImpl();

    public void viewJobs() {
        List<Job> jobs = jobDao.getAllJobs();
        jobs.forEach(j ->
                System.out.println(j.getId()+" | "+j.getTitle()+" | "+j.getCompany()+" | "+j.getLocation())
        );
    }

    public boolean createResume(Resume r) {
        return resumeDao.saveResume(r);
    }

    public boolean applyJob(int userId, int jobId) {
        return appDao.applyJob(userId, jobId);
    }

    public void viewApplications(int userId) {
        appDao.getApplicationsByUser(userId)
                .forEach(a -> System.out.println("AppID: "+a.getId()+" JobID: "+a.getJobId()+" Status: "+a.getStatus()));
    }

    public boolean withdraw(int appId) {
        return appDao.withdrawApplication(appId);
    }
}
