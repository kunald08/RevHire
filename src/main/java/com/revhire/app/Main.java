package com.revhire.app;

import com.revhire.model.*;
import com.revhire.service.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the RevHire Job Portal console application.
 * Provides separate registration flows and role-based menus
 * for Job Seekers and Employers.
 */
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    private static final Scanner sc = new Scanner(System.in);
    private static final AuthService authService = new AuthService();

    // ──────────────────────── Display Helpers ────────────────────────

    private static void banner(String title) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.printf( "║  %-48s║%n", title);
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    private static void line() {
        System.out.println("──────────────────────────────────────────────────");
    }

    private static void ok(String msg)   { System.out.println("  ✅ " + msg); }
    private static void fail(String msg) { System.out.println("  ❌ " + msg); }
    private static void hint(String msg) { System.out.println("  💡 " + msg); }
    private static void info(String msg) { System.out.println("  ℹ️  " + msg); }

    private static void menu(String... items) {
        for (int i = 0; i < items.length; i++) {
            System.out.printf("  [%02d]  %s%n", i + 1, items[i]);
        }
        line();
    }

    // ──────────────────────── Input Helpers ────────────────────────

    private static int readInt(String prompt) {
        while (true) {
            System.out.print("  " + prompt);
            try {
                int val = sc.nextInt();
                sc.nextLine();
                return val;
            } catch (InputMismatchException e) {
                sc.nextLine();
                fail("Please enter a valid number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print("  " + prompt);
            try {
                double val = sc.nextDouble();
                sc.nextLine();
                return val;
            } catch (InputMismatchException e) {
                sc.nextLine();
                fail("Please enter a valid number.");
            }
        }
    }

    private static String readLine(String prompt) {
        System.out.print("  " + prompt);
        return sc.nextLine();
    }

    /** Reads blank-means-null string for optional fields */
    private static String readOptional(String prompt) {
        String val = readLine(prompt);
        return val.isBlank() ? null : val;
    }

    private static Double readOptionalDouble(String prompt) {
        String val = readLine(prompt);
        if (val.isBlank()) return null;
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            fail("Invalid number, skipping filter.");
            return null;
        }
    }

    private static Integer readOptionalInt(String prompt) {
        String val = readLine(prompt);
        if (val.isBlank()) return null;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            fail("Invalid number, skipping filter.");
            return null;
        }
    }

    private static List<Integer> readIdList(String prompt) {
        String input = readLine(prompt);
        List<Integer> ids = new ArrayList<>();
        if (input.isBlank()) return ids;
        try {
            for (String s : input.split(",")) {
                ids.add(Integer.parseInt(s.trim()));
            }
        } catch (NumberFormatException e) {
            fail("Invalid number in list.");
            return new ArrayList<>();
        }
        return ids;
    }

    private static boolean confirm(String prompt) {
        String val = readLine(prompt + " (y/n): ").trim().toLowerCase();
        return val.equals("y") || val.equals("yes");
    }

    // ════════════════════════════ MAIN ════════════════════════════

    public static void main(String[] args) {
        logger.info("RevHire application started");

        while (true) {
            banner("RevHire — Job Portal");
            menu("Register as Job Seeker",
                 "Register as Employer",
                 "Login",
                 "Forgot Password",
                 "Exit");

            int choice = readInt("Choose ➜ ");

            switch (choice) {
                case 1  -> registerJobSeeker();
                case 2  -> registerEmployer();
                case 3  -> doLogin();
                case 4  -> doForgotPassword();
                case 5  -> { banner("Goodbye 👋"); logger.info("Exited by user"); return; }
                default -> fail("Invalid choice (1-5)");
            }
        }
    }

    // ──────────────────── Registration (Separate Flows) ──────────────────

    private static void registerJobSeeker() {
        banner("Register — Job Seeker");
        logger.info("Job Seeker registration started");

        User u = new User();
        u.setRole("JOB_SEEKER");
        u.setName(readLine("Full Name        : "));
        u.setEmail(readLine("Email            : "));
        u.setPassword(readLine("Password         : "));
        u.setPhone(readOptional("Phone (optional) : "));

        // Profile fields
        hint("Complete your profile now or later from the menu.");
        u.setEducation(readOptional("Education (optional)       : "));
        u.setWorkExperience(readOptional("Work Experience (optional) : "));
        u.setSkills(readOptional("Skills (optional)          : "));
        u.setCertifications(readOptional("Certifications (optional)  : "));

        // Security
        u.setSecurityQuestion(readLine("Security Question : "));
        u.setSecurityAnswer(readLine("Security Answer   : "));

        boolean done = authService.register(u);
        logger.info("Registration email={} result={}", u.getEmail(), done);

        if (done) {
            ok("Registration Successful — you can now login!");
            int pct = u.getProfileCompletion();
            info("Profile completion: " + pct + "%"
                    + (pct < 100 ? " — complete your profile after login for better matches." : ""));
        } else {
            fail("Registration Failed (email may already exist).");
        }
    }

    private static void registerEmployer() {
        banner("Register — Employer");
        logger.info("Employer registration started");

        User u = new User();
        u.setRole("EMPLOYER");
        u.setName(readLine("Full Name        : "));
        u.setEmail(readLine("Email            : "));
        u.setPassword(readLine("Password         : "));
        u.setPhone(readOptional("Phone (optional) : "));

        // Security
        u.setSecurityQuestion(readLine("Security Question : "));
        u.setSecurityAnswer(readLine("Security Answer   : "));

        boolean done = authService.register(u);
        logger.info("Registration email={} result={}", u.getEmail(), done);

        if (done) {
            ok("Registration Successful — you can now login!");
            hint("After login, create your Company Profile to start posting jobs.");
        } else {
            fail("Registration Failed (email may already exist).");
        }
    }

    // ──────────────────────── Login ────────────────────────

    private static void doLogin() {
        banner("Login");

        String email = readLine("Email    : ");
        String pwd   = readLine("Password : ");

        logger.info("Login attempt email={}", email);
        User user = authService.login(email, pwd);

        if (user == null) {
            logger.warn("Login failed email={}", email);
            fail("Invalid Credentials.");
            return;
        }

        logger.info("Login success userId={} role={}", user.getId(), user.getRole());
        ok("Welcome, " + user.getName() + "!");

        if (user.getRole().equalsIgnoreCase("JOB_SEEKER")) {
            jobSeekerMenu(user);
        } else {
            employerMenu(user);
        }
    }

    private static void doForgotPassword() {
        banner("Reset Password");

        String email = readLine("Email            : ");
        String q     = readLine("Security Question: ");
        String a     = readLine("Security Answer  : ");
        String np    = readLine("New Password     : ");

        logger.info("Forgot password email={}", email);
        boolean done = authService.forgotPassword(email, q, a, np);

        if (done) ok("Password Reset Successful — login with your new password.");
        else fail("Reset Failed — verify your email and security answer.");
    }

    // ════════════════════════ JOB SEEKER MENU ════════════════════════

    private static void jobSeekerMenu(User user) {
        JobSeekerService js = new JobSeekerService();
        logger.info("Entered Job Seeker menu userId={}", user.getId());

        boolean active = true;
        while (active) {
            // Show unread notification count
            int unread = js.getUnreadNotificationCount(user.getId());
            String notifBadge = unread > 0 ? " (" + unread + " new)" : "";

            banner("Job Seeker — " + user.getName());
            menu(
                "View All Jobs",
                "Search Jobs (with filters)",
                "View Job Details",
                "Create Resume",
                "Update Resume",
                "View My Resume",
                "Apply for a Job",
                "View My Applications",
                "Withdraw Application",
                "Manage Profile",
                "View Profile Completion",
                "Notifications" + notifBadge,
                "Mark All Notifications Read",
                "Change Password",
                "Logout"
            );

            int ch = readInt("Choose ➜ ");
            logger.info("JobSeeker choice={} userId={}", ch, user.getId());

            switch (ch) {
                case 1 -> {
                    banner("All Open Jobs");
                    js.viewAllJobs();
                }

                case 2 -> {
                    banner("Search Jobs");
                    hint("Leave any filter blank to skip it.");
                    String title    = readOptional("Job Role / Title  : ");
                    String loc      = readOptional("Location          : ");
                    String company  = readOptional("Company Name      : ");
                    String jt       = readOptional("Job Type (FULL_TIME/PART_TIME/INTERN): ");
                    Integer exp     = readOptionalInt("Min Experience (years) : ");
                    Double salMin   = readOptionalDouble("Min Salary        : ");
                    Double salMax   = readOptionalDouble("Max Salary        : ");
                    js.searchJobs(title, loc, jt, exp, company, salMin, salMax);
                }

                case 3 -> {
                    banner("Job Details");
                    int jobId = readInt("Job ID : ");
                    js.viewJobDetails(jobId);
                }

                case 4 -> {
                    banner("Create Resume");
                    hint("Sections: objective, education, experience, skills, projects.");
                    Resume r = new Resume();
                    r.setUserId(user.getId());
                    r.setObjective(readLine("Objective  : "));
                    r.setEducation(readLine("Education  : "));
                    r.setExperience(readLine("Experience : "));
                    r.setSkills(readLine("Skills     : "));
                    r.setProjects(readLine("Projects   : "));

                    logger.info("Creating resume userId={}", user.getId());
                    boolean done = js.createResume(r);
                    if (done) ok("Resume Created!");
                    else fail("Resume Creation Failed.");
                }

                case 5 -> {
                    banner("Update Resume");
                    Resume r = new Resume();
                    r.setUserId(user.getId());
                    r.setObjective(readLine("Objective  : "));
                    r.setEducation(readLine("Education  : "));
                    r.setExperience(readLine("Experience : "));
                    r.setSkills(readLine("Skills     : "));
                    r.setProjects(readLine("Projects   : "));

                    logger.info("Updating resume userId={}", user.getId());
                    boolean done = js.updateResume(r);
                    if (done) ok("Resume Updated!");
                    else fail("Resume Update Failed.");
                }

                case 6 -> {
                    banner("My Resume");
                    js.viewMyResume(user.getId());
                }

                case 7 -> {
                    banner("Apply for Job");
                    int jobId = readInt("Job ID : ");
                    hint("You can add an optional cover letter.");
                    String cover = readOptional("Cover Letter (blank = none) : ");

                    logger.info("Apply userId={} jobId={}", user.getId(), jobId);
                    boolean done = js.applyJob(user.getId(), jobId, cover);
                    if (done) ok("Application Submitted Successfully!");
                }

                case 8 -> {
                    banner("My Applications");
                    info("Status: APPLIED | SHORTLISTED | REJECTED | WITHDRAWN");
                    js.viewApplications(user.getId());
                }

                case 9 -> {
                    banner("Withdraw Application");
                    int appId = readInt("Application ID : ");
                    if (confirm("Are you sure you want to withdraw?")) {
                        String reason = readOptional("Reason (optional) : ");
                        logger.info("Withdraw appId={} userId={}", appId, user.getId());
                        boolean done = js.withdraw(appId, reason);
                        if (done) ok("Application Withdrawn.");
                        else fail("Withdraw Failed — only APPLIED status can be withdrawn.");
                    } else {
                        info("Withdraw cancelled.");
                    }
                }

                case 10 -> {
                    banner("Manage Profile");
                    // Reload latest profile from DB
                    User current = authService.getUserById(user.getId());
                    if (current == null) { fail("Could not load profile."); break; }

                    info("Current values shown in (brackets). Press Enter to keep.");
                    String name   = readLine("Name (" + current.getName() + ") : ");
                    String phone  = readLine("Phone (" + (current.getPhone() != null ? current.getPhone() : "—") + ") : ");
                    String edu    = readLine("Education (" + (current.getEducation() != null ? current.getEducation() : "—") + ") : ");
                    String workEx = readLine("Work Experience (" + (current.getWorkExperience() != null ? current.getWorkExperience() : "—") + ") : ");
                    String skills = readLine("Skills (" + (current.getSkills() != null ? current.getSkills() : "—") + ") : ");
                    String certs  = readLine("Certifications (" + (current.getCertifications() != null ? current.getCertifications() : "—") + ") : ");

                    current.setName(name.isBlank() ? current.getName() : name);
                    current.setPhone(phone.isBlank() ? current.getPhone() : phone);
                    current.setEducation(edu.isBlank() ? current.getEducation() : edu);
                    current.setWorkExperience(workEx.isBlank() ? current.getWorkExperience() : workEx);
                    current.setSkills(skills.isBlank() ? current.getSkills() : skills);
                    current.setCertifications(certs.isBlank() ? current.getCertifications() : certs);

                    logger.info("Update profile userId={}", user.getId());
                    boolean done = authService.updateProfile(current);
                    if (done) {
                        ok("Profile Updated!");
                        user.setName(current.getName()); // update local reference
                    } else {
                        fail("Profile Update Failed.");
                    }
                }

                case 11 -> {
                    banner("Profile Completion");
                    User current = authService.getUserById(user.getId());
                    if (current != null) {
                        int pct = current.getProfileCompletion();
                        System.out.println("  ┌─────────────────────────────────────┐");
                        System.out.printf( "  │  Profile Completion :  %3d%%          │%n", pct);
                        System.out.println("  └─────────────────────────────────────┘");
                        if (pct < 100) hint("Complete: phone, education, experience, skills, certifications.");
                        else ok("Your profile is 100% complete!");
                    }
                }

                case 12 -> {
                    banner("Notifications");
                    js.viewNotifications(user.getId());
                }

                case 13 -> {
                    js.markAllNotificationsRead(user.getId());
                    ok("All notifications marked as read.");
                }

                case 14 -> {
                    banner("Change Password");
                    String op = readLine("Current Password : ");
                    String np = readLine("New Password     : ");
                    logger.info("Change password userId={}", user.getId());
                    boolean done = authService.changePassword(user.getId(), op, np);
                    if (done) ok("Password Changed!");
                    else fail("Wrong current password.");
                }

                case 15 -> {
                    logger.info("JobSeeker logout userId={}", user.getId());
                    ok("Logged Out.");
                    active = false;
                }

                default -> fail("Invalid choice (1-15).");
            }
        }
    }

    // ════════════════════════ EMPLOYER MENU ════════════════════════

    private static void employerMenu(User user) {
        EmployerService es = new EmployerService();
        logger.info("Entered Employer menu userId={}", user.getId());

        boolean active = true;
        while (active) {
            int unread = es.getUnreadNotificationCount(user.getId());
            String notifBadge = unread > 0 ? " (" + unread + " new)" : "";

            banner("Employer — " + user.getName());
            menu(
                "Register Company Profile",
                "View Company Profile",
                "Update Company Profile",
                "Post a Job",
                "View My Jobs",
                "Edit a Job",
                "Close a Job",
                "Reopen a Job",
                "Delete a Job",
                "View Job Statistics",
                "View Applicants for a Job",
                "View Applicant Resume",
                "Filter Applicants",
                "Shortlist Applicant",
                "Reject Applicant",
                "Bulk Shortlist",
                "Bulk Reject",
                "Add Comment on Application",
                "Notifications" + notifBadge,
                "Change Password",
                "Logout"
            );

            int ch = readInt("Choose ➜ ");
            logger.info("Employer choice={} userId={}", ch, user.getId());

            switch (ch) {

                case 1 -> {
                    banner("Register Company Profile");
                    hint("Company details: name, industry, size, description, website, location.");
                    Company c = new Company();
                    c.setEmployerId(user.getId());
                    c.setName(readLine("Company Name : "));
                    c.setIndustry(readLine("Industry     : "));
                    hint("Size: e.g. 1-50, 51-200, 201-500, 500+");
                    c.setSize(readLine("Company Size : "));
                    c.setDescription(readLine("Description  : "));
                    c.setWebsite(readOptional("Website (optional) : "));
                    c.setLocation(readLine("Location     : "));

                    logger.info("Creating company userId={}", user.getId());
                    boolean done = es.createCompany(c);
                    if (done) ok("Company Profile Created!");
                    else fail("Failed (you may already have a company profile).");
                }

                case 2 -> {
                    banner("Company Profile");
                    es.viewCompanyProfile(user.getId());
                }

                case 3 -> {
                    banner("Update Company Profile");
                    Company current = es.getCompany(user.getId());
                    if (current == null) {
                        fail("No company profile found. Register one first.");
                        break;
                    }

                    info("Press Enter to keep current value.");
                    String name = readLine("Name (" + current.getName() + ") : ");
                    String ind  = readLine("Industry (" + current.getIndustry() + ") : ");
                    String size = readLine("Size (" + (current.getSize() != null ? current.getSize() : "—") + ") : ");
                    String desc = readLine("Description (" + (current.getDescription() != null ? current.getDescription() : "—") + ") : ");
                    String web  = readLine("Website (" + (current.getWebsite() != null ? current.getWebsite() : "—") + ") : ");
                    String loc  = readLine("Location (" + current.getLocation() + ") : ");

                    current.setName(name.isBlank() ? current.getName() : name);
                    current.setIndustry(ind.isBlank() ? current.getIndustry() : ind);
                    current.setSize(size.isBlank() ? current.getSize() : size);
                    current.setDescription(desc.isBlank() ? current.getDescription() : desc);
                    current.setWebsite(web.isBlank() ? current.getWebsite() : web);
                    current.setLocation(loc.isBlank() ? current.getLocation() : loc);

                    boolean done = es.updateCompany(current);
                    if (done) ok("Company Profile Updated!");
                    else fail("Update Failed.");
                }

                case 4 -> {
                    banner("Post Job");
                    hint("Fill in comprehensive job details.");
                    Job j = new Job();
                    j.setEmployerId(user.getId());
                    j.setTitle(readLine("Title             : "));
                    j.setDescription(readLine("Description       : "));
                    j.setCompany(readLine("Company Name      : "));
                    j.setLocation(readLine("Location          : "));
                    j.setSkillsRequired(readLine("Skills Required   : "));
                    j.setExperience(readInt("Experience (yrs)  : "));
                    j.setEducationReq(readOptional("Education Req.    : "));
                    j.setSalaryMin(readDouble("Salary Min (₹)    : "));
                    j.setSalaryMax(readDouble("Salary Max (₹)    : "));
                    hint("Types: FULL_TIME | PART_TIME | INTERN | CONTRACT");
                    j.setJobType(readLine("Job Type          : ").trim().toUpperCase());
                    j.setDeadline(readOptional("Deadline (YYYY-MM-DD, optional) : "));

                    logger.info("Posting job userId={}", user.getId());
                    boolean done = es.postJob(j);
                    if (done) ok("Job Posted Successfully!");
                    else fail("Job Posting Failed.");
                }

                case 5 -> {
                    banner("My Jobs");
                    es.viewMyJobs(user.getId());
                }

                case 6 -> {
                    banner("Edit Job");
                    int jobId = readInt("Job ID to edit : ");
                    Job existing = es.getJobById(jobId);
                    if (existing == null) { fail("Job not found."); break; }

                    info("Press Enter to keep current value.");
                    Job j = new Job();
                    j.setId(jobId);
                    j.setEmployerId(user.getId());

                    String t = readLine("Title (" + existing.getTitle() + ") : ");
                    j.setTitle(t.isBlank() ? existing.getTitle() : t);
                    String d = readLine("Description (" + (existing.getDescription() != null ? existing.getDescription() : "—") + ") : ");
                    j.setDescription(d.isBlank() ? existing.getDescription() : d);
                    String c = readLine("Company (" + existing.getCompany() + ") : ");
                    j.setCompany(c.isBlank() ? existing.getCompany() : c);
                    String l = readLine("Location (" + existing.getLocation() + ") : ");
                    j.setLocation(l.isBlank() ? existing.getLocation() : l);
                    String sk = readLine("Skills Required (" + (existing.getSkillsRequired() != null ? existing.getSkillsRequired() : "—") + ") : ");
                    j.setSkillsRequired(sk.isBlank() ? existing.getSkillsRequired() : sk);
                    String ex = readLine("Experience (" + existing.getExperience() + ") : ");
                    j.setExperience(ex.isBlank() ? existing.getExperience() : Integer.parseInt(ex));
                    String ed = readLine("Education Req (" + (existing.getEducationReq() != null ? existing.getEducationReq() : "—") + ") : ");
                    j.setEducationReq(ed.isBlank() ? existing.getEducationReq() : ed);
                    String smin = readLine("Salary Min (" + existing.getSalaryMin() + ") : ");
                    j.setSalaryMin(smin.isBlank() ? existing.getSalaryMin() : Double.parseDouble(smin));
                    String smax = readLine("Salary Max (" + existing.getSalaryMax() + ") : ");
                    j.setSalaryMax(smax.isBlank() ? existing.getSalaryMax() : Double.parseDouble(smax));
                    String jt = readLine("Job Type (" + existing.getJobType() + ") : ");
                    j.setJobType(jt.isBlank() ? existing.getJobType() : jt.trim().toUpperCase());
                    String dl = readLine("Deadline (" + (existing.getDeadline() != null ? existing.getDeadline() : "—") + ") : ");
                    j.setDeadline(dl.isBlank() ? existing.getDeadline() : dl);

                    logger.info("Edit job jobId={}", jobId);
                    boolean done = es.updateJob(j);
                    if (done) ok("Job Updated!");
                    else fail("Update Failed — verify Job ID & ownership.");
                }

                case 7 -> {
                    int jobId = readInt("Job ID to close : ");
                    logger.info("Close job jobId={}", jobId);
                    boolean done = es.closeJob(jobId);
                    if (done) ok("Job Closed.");
                    else fail("Close Failed.");
                }

                case 8 -> {
                    int jobId = readInt("Job ID to reopen : ");
                    logger.info("Reopen job jobId={}", jobId);
                    boolean done = es.reopenJob(jobId);
                    if (done) ok("Job Reopened.");
                    else fail("Reopen Failed.");
                }

                case 9 -> {
                    banner("Delete Job");
                    int jobId = readInt("Job ID to delete : ");
                    if (confirm("⚠️ This will permanently delete the job and all its applications. Proceed?")) {
                        logger.info("Delete job jobId={} userId={}", jobId, user.getId());
                        boolean done = es.deleteJob(jobId, user.getId());
                        if (done) ok("Job Deleted.");
                        else fail("Delete Failed — verify Job ID & ownership.");
                    } else {
                        info("Delete cancelled.");
                    }
                }

                case 10 -> {
                    banner("Job Statistics");
                    int jobId = readInt("Job ID : ");
                    es.viewJobStatistics(jobId);
                }

                case 11 -> {
                    banner("Applicants");
                    int jobId = readInt("Job ID : ");
                    es.viewApplicants(jobId);
                }

                case 12 -> {
                    banner("Applicant Resume");
                    int appId = readInt("Application ID : ");
                    es.viewApplicantResume(appId);
                }

                case 13 -> {
                    banner("Filter Applicants");
                    hint("Leave any filter blank to skip it.");
                    int jobId   = readInt("Job ID                         : ");
                    String sk   = readOptional("Skill (blank = any)            : ");
                    Integer exp = readOptionalInt("Min Experience (blank = any)   : ");
                    String edu  = readOptional("Education (blank = any)        : ");
                    String date = readOptional("Applied After (YYYY-MM-DD)     : ");
                    es.filterApplicants(jobId, sk, exp, edu, date);
                }

                case 14 -> {
                    int appId = readInt("Application ID to shortlist : ");
                    logger.info("Shortlist appId={}", appId);
                    boolean done = es.updateApplicationStatus(appId, "SHORTLISTED");
                    if (done) ok("Applicant Shortlisted!");
                    else fail("Shortlist Failed.");
                }

                case 15 -> {
                    int appId = readInt("Application ID to reject : ");
                    logger.info("Reject appId={}", appId);
                    boolean done = es.updateApplicationStatus(appId, "REJECTED");
                    if (done) ok("Applicant Rejected.");
                    else fail("Reject Failed.");
                }

                case 16 -> {
                    banner("Bulk Shortlist");
                    List<Integer> ids = readIdList("App IDs (comma-separated) : ");
                    if (ids.isEmpty()) { fail("No IDs provided."); break; }
                    logger.info("Bulk shortlist ids={}", ids);
                    int count = es.bulkShortlist(ids);
                    if (count > 0) ok(count + " applicant(s) shortlisted.");
                    else fail("Bulk Shortlist Failed.");
                }

                case 17 -> {
                    banner("Bulk Reject");
                    List<Integer> ids = readIdList("App IDs (comma-separated) : ");
                    if (ids.isEmpty()) { fail("No IDs provided."); break; }
                    logger.info("Bulk reject ids={}", ids);
                    int count = es.bulkReject(ids);
                    if (count > 0) ok(count + " applicant(s) rejected.");
                    else fail("Bulk Reject Failed.");
                }

                case 18 -> {
                    banner("Add Comment");
                    int appId      = readInt( "Application ID : ");
                    String comment = readLine("Comment        : ");
                    logger.info("Add comment appId={}", appId);
                    boolean done = es.addComment(appId, comment);
                    if (done) ok("Comment Added & applicant notified!");
                    else fail("Failed — check the Application ID.");
                }

                case 19 -> {
                    banner("Notifications");
                    es.viewNotifications(user.getId());
                    if (confirm("Mark all as read?")) {
                        es.markAllNotificationsRead(user.getId());
                        ok("All notifications marked as read.");
                    }
                }

                case 20 -> {
                    banner("Change Password");
                    String op = readLine("Current Password : ");
                    String np = readLine("New Password     : ");
                    logger.info("Change password userId={}", user.getId());
                    boolean done = authService.changePassword(user.getId(), op, np);
                    if (done) ok("Password Changed!");
                    else fail("Wrong current password.");
                }

                case 21 -> {
                    logger.info("Employer logout userId={}", user.getId());
                    ok("Logged Out.");
                    active = false;
                }

                default -> fail("Invalid choice (1-21).");
            }
        }
    }
}
