package com.revhire.service;

import com.revhire.dao.impl.*;
import com.revhire.model.*;

import java.util.List;

public class JobSeekerService {

    private JobDaoImpl jobDao = new JobDaoImpl();
    private ResumeDaoImpl resumeDao = new ResumeDaoImpl();
    private ApplicationDaoImpl appDao = new ApplicationDaoImpl();

    private NotificationDaoImpl notificationDao = new NotificationDaoImpl();

    // ================= JOBS =================

    public void viewAllJobs() {
        List<Job> jobs = jobDao.getAllJobs();
        jobs.forEach(j ->
                System.out.println(
                        j.getId() + " | " +
                                j.getTitle() + " | " +
                                j.getCompany() + " | " +
                                j.getLocation()
                )
        );
    }

    public void searchJobs(String title, String location, String type, Integer exp) {
        jobDao.searchJobs(title, location, type, exp)
                .forEach(j ->
                        System.out.println(
                                j.getId() + " | " +
                                        j.getTitle() + " | " +
                                        j.getCompany() + " | " +
                                        j.getLocation()
                        )
                );
    }

    // ================= RESUME =================

    public boolean createResume(Resume r) {
        return resumeDao.saveResume(r);
    }

    // ================= APPLICATION =================

    public boolean applyJob(int userId, int jobId) {
        return appDao.applyJob(userId, jobId);
    }

    public void viewApplications(int userId) {

        appDao.getApplicationsByUser(userId).forEach(a -> {

            System.out.println(
                    "AppID: " + a.getId() +
                            " | JobID: " + a.getJobId() +
                            " | Status: " + a.getStatus()
            );

            // ✅ SHOW COMMENT IF EXISTS
            if (a.getComment() != null && !a.getComment().isBlank()) {
                System.out.println("   💬 Comment: " + a.getComment());
            }

            System.out.println("--------------------------------");
        });
    }


    public boolean withdraw(int appId) {
        return appDao.withdrawApplication(appId);
    }

    // ================= NOTIFICATIONS =================

    public void viewNotifications(int userId) {
        notificationDao.getUserNotifications(userId)
                .forEach(n ->
                        System.out.println("🔔 " + n.getMessage())
                );
    }

    public void viewMyResume(int userId) {

        Resume r = resumeDao.getResumeByUserId(userId);

        if (r == null) {
            System.out.println("❌ No resume found");
            return;
        }

        System.out.println("\n===== MY RESUME =====");
        System.out.println("Objective: " + r.getObjective());
        System.out.println("Skills: " + r.getSkills());
        System.out.println("Experience: " + r.getExperience());
        System.out.println("Education: " + r.getEducation());
    }

}
