package com.revhire.service;

import com.revhire.dao.*;
import com.revhire.dao.impl.*;
import com.revhire.model.Application;
import com.revhire.model.Company;
import com.revhire.model.Job;
import com.revhire.model.Resume;

import java.util.List;

public class EmployerService {

    private CompanyDaoImpl companyDao = new CompanyDaoImpl();
    private JobDaoImpl jobDao = new JobDaoImpl();
    private ApplicationDaoImpl appDao = new ApplicationDaoImpl();
    private NotificationDaoImpl notificationDao = new NotificationDaoImpl();

    // ✅ MISSING DAO (THIS WAS THE BUG)
    private ResumeDaoImpl resumeDao = new ResumeDaoImpl();

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

    // ================= VIEW APPLICANTS =================
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

    public boolean updateJob(Job job) {
        return jobDao.updateJob(job);
    }

    public boolean closeJob(int jobId) {
        return jobDao.updateJobStatus(jobId, "CLOSED");
    }

    public boolean reopenJob(int jobId) {
        return jobDao.updateJobStatus(jobId, "OPEN");
    }

    // ================= JOB STATISTICS =================
    public void viewJobStatistics(int jobId) {

        int total =
                appDao.countByJobAndStatus(jobId, "APPLIED")
                        + appDao.countByJobAndStatus(jobId, "SHORTLISTED")
                        + appDao.countByJobAndStatus(jobId, "REJECTED");

        int shortlisted = appDao.countByJobAndStatus(jobId, "SHORTLISTED");
        int rejected = appDao.countByJobAndStatus(jobId, "REJECTED");

        System.out.println("\n📊 Job Statistics");
        System.out.println("Total Applications: " + total);
        System.out.println("Shortlisted: " + shortlisted);
        System.out.println("Rejected: " + rejected);
    }

    // ================= FILTER APPLICANTS =================
    public void filterApplicants(
            int jobId,
            String skill,
            Integer experience,
            String education) {

        var list = appDao.filterApplicants(jobId, skill, experience, education);

        if (list.isEmpty()) {
            System.out.println("❌ No matching applicants found");
            return;
        }

        for (Application a : list) {
            System.out.println(
                    "AppID: " + a.getId() +
                            " | Name: " + a.getApplicantName() +
                            " | Email: " + a.getApplicantEmail() +
                            " | Status: " + a.getStatus()
            );
        }
    }

    // ================= BULK ACTIONS =================
    public boolean bulkShortlist(List<Integer> appIds) {
        return appDao.updateStatusBulk(appIds, "SHORTLISTED");
    }

    public boolean bulkReject(List<Integer> appIds) {
        return appDao.updateStatusBulk(appIds, "REJECTED");
    }

    // ================= COMMENT =================
    public boolean addComment(int appId, String comment) {
        return appDao.addComment(appId, comment);
    }

    // ================= STATUS + NOTIFICATION =================
    public boolean updateApplicationStatus(int appId, String status) {

        boolean updated = appDao.updateStatus(appId, status);

        if (updated) {
            int userId = appDao.getUserIdByApplication(appId);
            notificationDao.createNotification(
                    userId,
                    "Your application status changed to " + status
            );
        }
        return updated;
    }

    // ================= VIEW APPLICANT RESUME =================
    public void viewApplicantResume(int applicationId) {

        Resume r = resumeDao.getResumeByApplicationId(applicationId);

        if (r == null) {
            System.out.println("❌ Resume not found");
            return;
        }

        System.out.println("\n===== APPLICANT RESUME =====");
        System.out.println("Objective  : " + r.getObjective());
        System.out.println("Skills     : " + r.getSkills());
        System.out.println("Experience : " + r.getExperience());
        System.out.println("Education  : " + r.getEducation());
    }
}
