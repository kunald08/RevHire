package com.revhire.app;

import com.revhire.model.*;
import com.revhire.service.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        AuthService authService = new AuthService();

        while (true) {
            System.out.println("\n=== RevHire Job Portal ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                // ================= REGISTER =================
                case 1 -> {
                    User u = new User();

                    System.out.print("Name: ");
                    u.setName(sc.nextLine());

                    System.out.print("Email: ");
                    u.setEmail(sc.nextLine());

                    System.out.print("Password: ");
                    u.setPassword(sc.nextLine());

                    System.out.print("Role (JOB_SEEKER / EMPLOYER): ");
                    u.setRole(sc.nextLine());

                    if (authService.register(u))
                        System.out.println("✅ Registration successful");
                    else
                        System.out.println("❌ Registration failed");
                }

                // ================= LOGIN =================
                case 2 -> {
                    System.out.print("Email: ");
                    String email = sc.nextLine();

                    System.out.print("Password: ");
                    String password = sc.nextLine();

                    User user = authService.login(email, password);

                    if (user == null) {
                        System.out.println("❌ Invalid credentials");
                        break;
                    }

                    System.out.println("✅ Welcome " + user.getName()
                            + " (" + user.getRole() + ")");

                    // =====================================================
                    // ================= JOB SEEKER ========================
                    // =====================================================
                    if (user.getRole().equalsIgnoreCase("JOB_SEEKER")) {

                        JobSeekerService js = new JobSeekerService();

                        while (true) {
                            System.out.println("\n--- Job Seeker Menu ---");
                            System.out.println("1. View All Jobs");
                            System.out.println("2. Search Jobs");
                            System.out.println("3. Create Resume");
                            System.out.println("4. Apply for Job");
                            System.out.println("5. View Applications");
                            System.out.println("6. Withdraw Application");
                            System.out.println("7. Logout");

                            int ch = sc.nextInt();
                            sc.nextLine();

                            switch (ch) {

                                case 1 -> js.viewAllJobs();

                                case 2 -> {
                                    System.out.print("Title (blank = any): ");
                                    String title = sc.nextLine();

                                    System.out.print("Location (blank = any): ");
                                    String location = sc.nextLine();

                                    System.out.print("Job Type (FULL_TIME / PART_TIME / blank): ");
                                    String type = sc.nextLine();

                                    System.out.print("Min Experience (0 = any): ");
                                    int expIn = sc.nextInt();
                                    sc.nextLine();

                                    Integer exp = (expIn <= 0) ? null : expIn;
                                    js.searchJobs(title, location, type, exp);
                                }

                                case 3 -> {
                                    Resume r = new Resume();
                                    r.setUserId(user.getId());

                                    System.out.print("Objective: ");
                                    r.setObjective(sc.nextLine());

                                    System.out.print("Skills: ");
                                    r.setSkills(sc.nextLine());

                                    System.out.print("Experience: ");
                                    r.setExperience(sc.nextLine());

                                    System.out.print("Education: ");
                                    r.setEducation(sc.nextLine());

                                    if (js.createResume(r))
                                        System.out.println("✅ Resume created");
                                    else
                                        System.out.println("❌ Resume failed");
                                }

                                case 4 -> {
                                    System.out.print("Job ID: ");
                                    int jobId = sc.nextInt();
                                    sc.nextLine();

                                    if (js.applyJob(user.getId(), jobId))
                                        System.out.println("✅ Applied successfully");
                                    else
                                        System.out.println("❌ Application failed");
                                }

                                case 5 -> js.viewApplications(user.getId());

                                case 6 -> {
                                    System.out.print("Application ID: ");
                                    int appId = sc.nextInt();
                                    sc.nextLine();

                                    if (js.withdraw(appId))
                                        System.out.println("✅ Application withdrawn");
                                    else
                                        System.out.println("❌ Withdraw failed");
                                }

                                case 7 -> {
                                    System.out.println("🔒 Logged out");
                                    break;
                                }
                            }
                            if (ch == 7) break;
                        }
                    }

                    // =====================================================
                    // ================= EMPLOYER ==========================
                    // =====================================================
                    else if (user.getRole().equalsIgnoreCase("EMPLOYER")) {

                        EmployerService es = new EmployerService();

                        while (true) {
                            System.out.println("\n--- Employer Menu ---");
                            System.out.println("1. Create Company");
                            System.out.println("2. Post Job");
                            System.out.println("3. View My Jobs");
                            System.out.println("4. View Applicants");
                            System.out.println("5. Shortlist Applicant");
                            System.out.println("6. Reject Applicant");
                            System.out.println("7. Logout");

                            int ch = sc.nextInt();
                            sc.nextLine();

                            switch (ch) {

                                case 1 -> {
                                    Company c = new Company();
                                    c.setEmployerId(user.getId());

                                    System.out.print("Company Name: ");
                                    c.setName(sc.nextLine());

                                    System.out.print("Industry: ");
                                    c.setIndustry(sc.nextLine());

                                    System.out.print("Location: ");
                                    c.setLocation(sc.nextLine());

                                    System.out.print("Description: ");
                                    c.setDescription(sc.nextLine());

                                    if (es.createCompany(c))
                                        System.out.println("✅ Company created");
                                    else
                                        System.out.println("❌ Company creation failed");
                                }

                                case 2 -> {
                                    Job j = new Job();
                                    j.setEmployerId(user.getId());

                                    System.out.print("Job Title: ");
                                    j.setTitle(sc.nextLine());

                                    System.out.print("Company Name: ");
                                    j.setCompany(sc.nextLine());

                                    System.out.print("Location: ");
                                    j.setLocation(sc.nextLine());

                                    System.out.print("Experience: ");
                                    j.setExperience(sc.nextInt());

                                    System.out.print("Salary: ");
                                    j.setSalary(sc.nextDouble());
                                    sc.nextLine();

                                    System.out.print("Job Type: ");
                                    j.setJobType(sc.nextLine());

                                    if (es.postJob(j))
                                        System.out.println("✅ Job posted");
                                    else
                                        System.out.println("❌ Job posting failed");
                                }

                                case 3 -> es.viewMyJobs(user.getId());

                                case 4 -> {
                                    System.out.print("Job ID: ");
                                    int jobId = sc.nextInt();
                                    sc.nextLine();
                                    es.viewApplicants(jobId);
                                }

                                case 5 -> {
                                    System.out.print("Application ID: ");
                                    int appId = sc.nextInt();
                                    sc.nextLine();
                                    es.updateApplicationStatus(appId, "SHORTLISTED");
                                    System.out.println("✅ Applicant shortlisted");
                                }

                                case 6 -> {
                                    System.out.print("Application ID: ");
                                    int appId = sc.nextInt();
                                    sc.nextLine();
                                    es.updateApplicationStatus(appId, "REJECTED");
                                    System.out.println("❌ Applicant rejected");
                                }

                                case 7 -> {
                                    System.out.println("🔒 Logged out");
                                    break;
                                }
                            }
                            if (ch == 7) break;
                        }
                    }
                }

                // ================= EXIT =================
                case 3 -> {
                    System.out.println("👋 Thank you for using RevHire");
                    System.exit(0);
                }
            }
        }
    }
}
