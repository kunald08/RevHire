package com.revhire.service;

import com.revhire.dao.*;
import com.revhire.dao.impl.*;
import com.revhire.model.Company;
import com.revhire.model.Job;
import com.revhire.model.Resume;

import java.util.List;

/**
 * Handles all Employer operations — company, jobs, applicants, statistics, notifications.
 */
public class EmployerService {

    private final CompanyDao      companyDao      = new CompanyDaoImpl();
    private final JobDao          jobDao          = new JobDaoImpl();
    private final ApplicationDao  appDao          = new ApplicationDaoImpl();
    private final ResumeDao       resumeDao       = new ResumeDaoImpl();
    private final NotificationDao notificationDao = new NotificationDaoImpl();

    // ── Company ──

    public boolean createCompany(Company c) {
        return companyDao.createCompany(c);
    }

    public Company getCompany(int employerId) {
        return companyDao.getCompanyByEmployerId(employerId);
    }

    public void viewCompanyProfile(int employerId) {
        Company c = companyDao.getCompanyByEmployerId(employerId);
        if (c == null) {
            System.out.println("  No company profile found. Create one first.");
            return;
        }
        System.out.println(c);
    }

    public boolean updateCompany(Company c) {
        return companyDao.updateCompany(c);
    }

    // ── Job Management ──

    public boolean postJob(Job j) {
        boolean done = jobDao.createJob(j);
        if (done) {
            // Notify: could expand to match seekers by skills/location
            // For now, just log it
        }
        return done;
    }

    public void viewMyJobs(int employerId) {
        var jobs = jobDao.getJobsByEmployer(employerId);
        if (jobs.isEmpty()) {
            System.out.println("  You haven't posted any jobs yet.");
            return;
        }
        System.out.printf("  %-4s │ %-22s │ %-12s │ %-12s │ %-14s │ %s%n",
                "ID", "Title", "Company", "Location", "Salary", "Status");
        System.out.println("  " + "─".repeat(85));
        jobs.forEach(j -> System.out.printf("  %-4d │ %-22s │ %-12s │ %-12s │ ₹%.0f-%.0f │ %s%n",
                j.getId(), j.getTitle(), j.getCompany(), j.getLocation(),
                j.getSalaryMin(), j.getSalaryMax(),
                j.getStatus() != null ? j.getStatus() : "OPEN"));
    }

    public Job getJobById(int jobId) {
        return jobDao.getJobById(jobId);
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

    public boolean deleteJob(int jobId, int employerId) {
        return jobDao.deleteJob(jobId, employerId);
    }

    // ── Applicant Management ──

    public void viewApplicants(int jobId) {
        var apps = appDao.getApplicationsByJob(jobId);
        if (apps.isEmpty()) {
            System.out.println("  No applicants for this job yet.");
            return;
        }
        System.out.printf("  %-6s │ %-20s │ %-25s │ %-12s │ %-16s │ %s%n",
                "AppID", "Name", "Email", "Status", "Applied At", "Comment");
        System.out.println("  " + "─".repeat(110));
        apps.forEach(a -> System.out.printf("  %-6d │ %-20s │ %-25s │ %-12s │ %-16s │ %s%n",
                a.getId(), a.getApplicantName(), a.getApplicantEmail(),
                a.getStatus(), a.getAppliedAt() != null ? a.getAppliedAt() : "—",
                a.getComment() != null ? a.getComment() : "—"));
    }

    public void filterApplicants(int jobId, String skill, Integer experience,
                                 String education, String dateFrom) {
        var list = appDao.filterApplicants(jobId, skill, experience, education, dateFrom);
        if (list.isEmpty()) {
            System.out.println("  No applicants match the criteria.");
            return;
        }
        System.out.printf("  %-6s │ %-20s │ %-25s │ %-12s │ %s%n",
                "AppID", "Name", "Email", "Status", "Applied At");
        System.out.println("  " + "─".repeat(90));
        list.forEach(a -> System.out.printf("  %-6d │ %-20s │ %-25s │ %-12s │ %s%n",
                a.getId(), a.getApplicantName(), a.getApplicantEmail(),
                a.getStatus(), a.getAppliedAt() != null ? a.getAppliedAt() : "—"));
    }

    public boolean updateApplicationStatus(int appId, String status) {
        boolean updated = appDao.updateStatus(appId, status);
        if (updated) {
            int userId = appDao.getUserIdByApplication(appId);
            if (userId > 0) {
                notificationDao.createNotification(userId,
                        "Your application (ID: " + appId + ") has been " + status);
            }
        }
        return updated;
    }

    // ── Bulk Actions ──

    public int bulkShortlist(List<Integer> appIds) {
        boolean done = appDao.updateStatusBulk(appIds, "SHORTLISTED");
        if (done) {
            for (int appId : appIds) {
                int userId = appDao.getUserIdByApplication(appId);
                if (userId > 0) {
                    notificationDao.createNotification(userId,
                            "Your application (ID: " + appId + ") has been SHORTLISTED");
                }
            }
        }
        return done ? appIds.size() : 0;
    }

    public int bulkReject(List<Integer> appIds) {
        boolean done = appDao.updateStatusBulk(appIds, "REJECTED");
        if (done) {
            for (int appId : appIds) {
                int userId = appDao.getUserIdByApplication(appId);
                if (userId > 0) {
                    notificationDao.createNotification(userId,
                            "Your application (ID: " + appId + ") has been REJECTED");
                }
            }
        }
        return done ? appIds.size() : 0;
    }

    // ── Comment ──

    public boolean addComment(int appId, String comment) {
        boolean done = appDao.addComment(appId, comment);
        if (done) {
            int userId = appDao.getUserIdByApplication(appId);
            if (userId > 0) {
                notificationDao.createNotification(userId,
                        "New comment on your application (ID: " + appId + "): " + comment);
            }
        }
        return done;
    }

    // ── Statistics ──

    public void viewJobStatistics(int jobId) {
        int applied     = appDao.countByJobAndStatus(jobId, "APPLIED");
        int shortlisted = appDao.countByJobAndStatus(jobId, "SHORTLISTED");
        int rejected    = appDao.countByJobAndStatus(jobId, "REJECTED");
        int withdrawn   = appDao.countByJobAndStatus(jobId, "WITHDRAWN");
        int total       = applied + shortlisted + rejected + withdrawn;

        System.out.println("  ┌─────────────────────────────────────┐");
        System.out.printf( "  │  Total Applications  :  %-10d  │%n", total);
        System.out.printf( "  │  Applied             :  %-10d  │%n", applied);
        System.out.printf( "  │  Shortlisted         :  %-10d  │%n", shortlisted);
        System.out.printf( "  │  Rejected            :  %-10d  │%n", rejected);
        System.out.printf( "  │  Withdrawn           :  %-10d  │%n", withdrawn);
        System.out.println("  └─────────────────────────────────────┘");
    }

    // ── Resume Viewing ──

    public void viewApplicantResume(int applicationId) {
        Resume r = resumeDao.getResumeByApplicationId(applicationId);
        if (r == null) {
            System.out.println("  No resume found for this applicant.");
            return;
        }
        System.out.println(r);
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

    public boolean markAllNotificationsRead(int userId) {
        return notificationDao.markAllAsRead(userId);
    }
}
