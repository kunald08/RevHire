package com.revhire.model;

/**
 * Represents a job application submitted by a Job Seeker.
 * Statuses: APPLIED, SHORTLISTED, REJECTED, WITHDRAWN.
 */
public class Application {

    private int    id;
    private int    userId;
    private int    jobId;
    private String coverLetter;
    private String status;
    private String comment;
    private String withdrawReason;
    private String appliedAt;

    // Fields populated via JOINs for display
    private String applicantName;
    private String applicantEmail;
    private String jobTitle;
    private String jobCompany;

    // ── Getters ──

    public int    getId()              { return id; }
    public int    getUserId()          { return userId; }
    public int    getJobId()           { return jobId; }
    public String getCoverLetter()     { return coverLetter; }
    public String getStatus()          { return status; }
    public String getComment()         { return comment; }
    public String getWithdrawReason()  { return withdrawReason; }
    public String getAppliedAt()       { return appliedAt; }
    public String getApplicantName()   { return applicantName; }
    public String getApplicantEmail()  { return applicantEmail; }
    public String getJobTitle()        { return jobTitle; }
    public String getJobCompany()      { return jobCompany; }

    // ── Setters ──

    public void setId(int id)                         { this.id = id; }
    public void setUserId(int userId)                 { this.userId = userId; }
    public void setJobId(int jobId)                   { this.jobId = jobId; }
    public void setCoverLetter(String coverLetter)    { this.coverLetter = coverLetter; }
    public void setStatus(String status)              { this.status = status; }
    public void setComment(String comment)            { this.comment = comment; }
    public void setWithdrawReason(String reason)      { this.withdrawReason = reason; }
    public void setAppliedAt(String appliedAt)        { this.appliedAt = appliedAt; }
    public void setApplicantName(String name)         { this.applicantName = name; }
    public void setApplicantEmail(String email)       { this.applicantEmail = email; }
    public void setJobTitle(String title)             { this.jobTitle = title; }
    public void setJobCompany(String company)         { this.jobCompany = company; }

    @Override
    public String toString() {
        return String.format("  %-4d │ %-20s │ %-12s │ %-12s │ %s │ %s",
                id, jobTitle != null ? jobTitle : "Job#" + jobId,
                jobCompany != null ? jobCompany : "—",
                status, appliedAt != null ? appliedAt : "—",
                comment != null ? comment : "");
    }
}
