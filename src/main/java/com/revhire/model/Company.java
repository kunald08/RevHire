package com.revhire.model;

/**
 * Represents a company profile created by an Employer.
 * Includes: name, industry, size, description, website, location.
 */
public class Company {

    private int    id;
    private int    employerId;
    private String name;
    private String industry;
    private String size;
    private String description;
    private String website;
    private String location;

    // ── Getters ──

    public int    getId()          { return id; }
    public int    getEmployerId()  { return employerId; }
    public String getName()        { return name; }
    public String getIndustry()    { return industry; }
    public String getSize()        { return size; }
    public String getDescription() { return description; }
    public String getWebsite()     { return website; }
    public String getLocation()    { return location; }

    // ── Setters ──

    public void setId(int id)                      { this.id = id; }
    public void setEmployerId(int employerId)      { this.employerId = employerId; }
    public void setName(String name)               { this.name = name; }
    public void setIndustry(String industry)       { this.industry = industry; }
    public void setSize(String size)               { this.size = size; }
    public void setDescription(String description) { this.description = description; }
    public void setWebsite(String website)         { this.website = website; }
    public void setLocation(String location)       { this.location = location; }

    @Override
    public String toString() {
        return String.format("""
                  Name     : %s
                  Industry : %s
                  Size     : %s
                  Location : %s
                  Website  : %s
                  About    : %s""",
                name, industry, size, location, website, description);
    }
}
