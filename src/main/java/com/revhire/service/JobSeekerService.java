package com.revhire.service;

import com.revhire.dao.*;
import com.revhire.dao.impl.*;
import com.revhire.model.Job;
import com.revhire.model.Resume;

import java.util.List;

/**
 * Handles all Job Seeker operations — jobs, resume, applications, notifications, profile.
 */
public class JobSeekerService {

    private final JobDao          jobDao          = new JobDaoImpl();
    private final ResumeDao       resumeDao       = new ResumeDaoImpl();
    private final ApplicationDao  appDao          = new ApplicationDaoImpl();
    private final NotificationDao notificationDao = new NotificationDaoImpl();

    // ── Jobs ──

    public void viewAllJobs() {
        var jobs = jobDao.getAllJobs();
        if (jobs.isEmpty()) {
            System.out.println("  No jobs available right now.");
            return;
        }
        printJobHeader();
        jobs.forEach(j -> System.out.println("  " + j));
    }

    public void searchJobs(String title, String location, String jobType,
                           Integer exp, String company, Double salaryMin, Double salaryMax) {
        var jobs = jobDao.searchJobs(title, location, jobType, exp, company, salaryMin, salaryMax);
        if (jobs.isEmpty()) {
            System.out.println("  No matching jobs found.");
            return;
        }
        printJobHeader();
        jobs.forEach(j -> System.out.println("  " + j));
    }

    public void viewJobDetails(int jobId) {
        Job j = jobDao.getJobById(jobId);
        if (j == null) {
            System.out.println("  Job not found.");
            return;
        }
        System.out.println("  Title        : " + j.getTitle());
        System.out.println("  Company      : " + j.getCompany());
        System.out.println("  Description  : " + (j.getDescription() != null ? j.getDescription() : "—"));
        System.out.println("  Location     : " + j.getLocation());
        System.out.println("  Skills Req.  : " + (j.getSkillsRequired() != null ? j.getSkillsRequired() : "—"));
        System.out.println("  Experience   : " + j.getExperience() + " years");
        System.out.println("  Education    : " + (j.getEducationReq() != null ? j.getEducationReq() : "—"));
        System.out.printf( "  Salary       : ₹%.0f – ₹%.0f%n", j.getSalaryMin(), j.getSalaryMax());
        System.out.println("  Type         : " + j.getJobType());
        System.out.println("  Deadline     : " + (j.getDeadline() != null ? j.getDeadline() : "—"));
        System.out.println("  Status       : " + j.getStatus());
    }

    private void printJobHeader() {
        System.out.printf("  %-4s │ %-22s │ %-12s │ %-12s │ %-6s │ %-14s │ %-9s │ %s%n",
                "ID", "Title", "Company", "Location", "Exp", "Salary", "Type", "Status");
        System.out.println("  " + "─".repeat(110));
    }

    // ── Resume ──

    public boolean createResume(Resume r) {
        if (resumeDao.hasResume(r.getUserId())) {
            System.out.println("  ⚠️  You already have a resume. Use 'Update Resume' instead.");
            return false;
        }
        return resumeDao.saveResume(r);
    }

    public boolean updateResume(Resume r) {
        if (!resumeDao.hasResume(r.getUserId())) {
            System.out.println("  ⚠️  No resume found. Use 'Create Resume' first.");
            return false;
        }
        return resumeDao.updateResume(r);
    }

    public void viewMyResume(int userId) {
        Resume r = resumeDao.getResumeByUserId(userId);
        if (r == null) {
            System.out.println("  You haven't created a resume yet.");
            return;
        }
        System.out.println(r);
    }

    // ── Applications ──

    public boolean applyJob(int userId, int jobId, String coverLetter) {
        if (!resumeDao.hasResume(userId)) {
            System.out.println("  ❌ Create a resume first before applying.");
            return false;
        }
        if (appDao.alreadyApplied(userId, jobId)) {
            System.out.println("  ❌ You've already applied for this job.");
            return false;
        }
        boolean done = appDao.applyJob(userId, jobId, coverLetter);
        if (done) {
            notificationDao.createNotification(userId,
                    "You have successfully applied for Job #" + jobId);
        }
        return done;
    }

    public void viewApplications(int userId) {
        var apps = appDao.getApplicationsByUser(userId);
        if (apps.isEmpty()) {
            System.out.println("  No applications yet.");
            return;
        }
        System.out.printf("  %-4s │ %-20s │ %-12s │ %-12s │ %-16s │ %s%n",
                "ID", "Job Title", "Company", "Status", "Applied At", "Comment");
        System.out.println("  " + "─".repeat(95));
        apps.forEach(System.out::println);
    }

    public boolean withdraw(int appId, String reason) {
        return appDao.withdrawApplication(appId, reason);
    }

    // ── Notifications ──

    public int getUnreadNotificationCount(int userId) {
        return notificationDao.getUnreadCount(userId);
    }

    public void viewNotifications(int userId) {
        var notes = notificationDao.getUserNotifications(userId);
        if (notes.isEmpty()) {
            System.out.println("  No notifications.");
            return;
        }
        notes.forEach(System.out::println);
    }

    public boolean markNotificationRead(int notifId) {
        return notificationDao.markAsRead(notifId);
    }

    public boolean markAllNotificationsRead(int userId) {
        return notificationDao.markAllAsRead(userId);
    }
}
