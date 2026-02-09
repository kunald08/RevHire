package com.revhire.model;

/**
 * Represents a user in the system (Job Seeker or Employer).
 * Includes profile fields: education, work experience, skills, certifications.
 */
public class User {

    private int    id;
    private String name;
    private String email;
    private String password;
    private String role;
    private String phone;
    private String education;
    private String workExperience;
    private String skills;
    private String certifications;
    private String securityQuestion;
    private String securityAnswer;

    // ── Getters ──

    public int    getId()              { return id; }
    public String getName()            { return name; }
    public String getEmail()           { return email; }
    public String getPassword()        { return password; }
    public String getRole()            { return role; }
    public String getPhone()           { return phone; }
    public String getEducation()       { return education; }
    public String getWorkExperience()  { return workExperience; }
    public String getSkills()          { return skills; }
    public String getCertifications()  { return certifications; }
    public String getSecurityQuestion(){ return securityQuestion; }
    public String getSecurityAnswer()  { return securityAnswer; }

    // ── Setters ──

    public void setId(int id)                          { this.id = id; }
    public void setName(String name)                   { this.name = name; }
    public void setEmail(String email)                 { this.email = email; }
    public void setPassword(String password)           { this.password = password; }
    public void setRole(String role)                   { this.role = role; }
    public void setPhone(String phone)                 { this.phone = phone; }
    public void setEducation(String education)         { this.education = education; }
    public void setWorkExperience(String workExp)      { this.workExperience = workExp; }
    public void setSkills(String skills)               { this.skills = skills; }
    public void setCertifications(String certifications){ this.certifications = certifications; }
    public void setSecurityQuestion(String question)   { this.securityQuestion = question; }
    public void setSecurityAnswer(String answer)       { this.securityAnswer = answer; }

    /**
     * Calculates profile completion percentage for Job Seekers.
     * Fields checked: phone, education, workExperience, skills, certifications.
     */
    public int getProfileCompletion() {
        int filled = 0;
        int total  = 5;
        if (phone != null && !phone.isBlank())          filled++;
        if (education != null && !education.isBlank())  filled++;
        if (workExperience != null && !workExperience.isBlank()) filled++;
        if (skills != null && !skills.isBlank())        filled++;
        if (certifications != null && !certifications.isBlank()) filled++;
        return (filled * 100) / total;
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s', role='%s'}", id, name, email, role);
    }
}
