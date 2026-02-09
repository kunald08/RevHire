package com.revhire.model;

/**
 * Represents a job posting created by an Employer.
 * Includes comprehensive details: description, skills, education, salary range, deadline.
 */
public class Job {

    private int    id;
    private int    employerId;
    private String title;
    private String description;
    private String company;
    private String location;
    private String skillsRequired;
    private int    experience;
    private String educationReq;
    private double salaryMin;
    private double salaryMax;
    private String jobType;
    private String status;
    private String deadline;

    // ── Getters ──

    public int    getId()             { return id; }
    public int    getEmployerId()     { return employerId; }
    public String getTitle()          { return title; }
    public String getDescription()    { return description; }
    public String getCompany()        { return company; }
    public String getLocation()       { return location; }
    public String getSkillsRequired() { return skillsRequired; }
    public int    getExperience()     { return experience; }
    public String getEducationReq()   { return educationReq; }
    public double getSalaryMin()      { return salaryMin; }
    public double getSalaryMax()      { return salaryMax; }
    public String getJobType()        { return jobType; }
    public String getStatus()         { return status; }
    public String getDeadline()       { return deadline; }

    // ── Setters ──

    public void setId(int id)                       { this.id = id; }
    public void setEmployerId(int empId)            { this.employerId = empId; }
    public void setTitle(String title)              { this.title = title; }
    public void setDescription(String desc)         { this.description = desc; }
    public void setCompany(String company)          { this.company = company; }
    public void setLocation(String loc)             { this.location = loc; }
    public void setSkillsRequired(String skills)    { this.skillsRequired = skills; }
    public void setExperience(int exp)              { this.experience = exp; }
    public void setEducationReq(String eduReq)      { this.educationReq = eduReq; }
    public void setSalaryMin(double min)            { this.salaryMin = min; }
    public void setSalaryMax(double max)            { this.salaryMax = max; }
    public void setJobType(String jobType)          { this.jobType = jobType; }
    public void setStatus(String status)            { this.status = status; }
    public void setDeadline(String deadline)        { this.deadline = deadline; }

    @Override
    public String toString() {
        return String.format("%-4d │ %-22s │ %-12s │ %-12s │ %d yrs │ ₹%.0f-%.0f │ %-9s │ %s",
                id, title, company, location, experience, salaryMin, salaryMax, jobType,
                status != null ? status : "OPEN");
    }
}
