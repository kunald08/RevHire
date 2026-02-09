package com.revhire.model;

/**
 * Represents a Job Seeker's resume with structured sections.
 * Sections: objective, education, experience, skills, projects.
 */
public class Resume {

    private int    id;
    private int    userId;
    private String objective;
    private String education;
    private String experience;
    private String skills;
    private String projects;

    // ── Getters ──

    public int    getId()         { return id; }
    public int    getUserId()     { return userId; }
    public String getObjective()  { return objective; }
    public String getEducation()  { return education; }
    public String getExperience() { return experience; }
    public String getSkills()     { return skills; }
    public String getProjects()   { return projects; }

    // ── Setters ──

    public void setId(int id)                    { this.id = id; }
    public void setUserId(int userId)            { this.userId = userId; }
    public void setObjective(String objective)   { this.objective = objective; }
    public void setEducation(String education)   { this.education = education; }
    public void setExperience(String experience) { this.experience = experience; }
    public void setSkills(String skills)         { this.skills = skills; }
    public void setProjects(String projects)     { this.projects = projects; }

    @Override
    public String toString() {
        return String.format("""
                  Objective  : %s
                  Education  : %s
                  Experience : %s
                  Skills     : %s
                  Projects   : %s""",
                objective, education, experience, skills, projects);
    }
}
