package com.revhire.app;

import com.revhire.model.*;
import com.revhire.service.*;

import java.util.*;

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

                    System.out.println(
                            authService.register(u)
                                    ? "✅ Registration successful"
                                    : "❌ Registration failed"
                    );
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

                    System.out.println("✅ Welcome " + user.getName() +
                            " (" + user.getRole() + ")");

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
                            System.out.println("7. View Notifications");
                            System.out.println("8. Logout");

                            int ch = sc.nextInt();
                            sc.nextLine();

                            switch (ch) {

                                case 1 -> js.viewAllJobs();

                                case 2 -> {
                                    System.out.print("Title: ");
                                    String title = sc.nextLine();

                                    System.out.print("Location: ");
                                    String location = sc.nextLine();

                                    System.out.print("Job Type: ");
                                    String type = sc.nextLine();

                                    System.out.print("Min Experience (0 = any): ");
                                    int e = sc.nextInt();
                                    sc.nextLine();

                                    js.searchJobs(title, location, type,
                                            e <= 0 ? null : e);
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

                                    System.out.println(
                                            js.createResume(r)
                                                    ? "✅ Resume created"
                                                    : "❌ Resume failed"
                                    );
                                }

                                case 4 -> {
                                    System.out.print("Job ID: ");
                                    int jobId = sc.nextInt();
                                    sc.nextLine();

                                    System.out.println(
                                            js.applyJob(user.getId(), jobId)
                                                    ? "✅ Applied successfully"
                                                    : "❌ Application failed"
                                    );
                                }

                                case 5 -> js.viewApplications(user.getId());

                                case 6 -> {
                                    System.out.print("Application ID: ");
                                    int appId = sc.nextInt();
                                    sc.nextLine();

                                    System.out.println(
                                            js.withdraw(appId)
                                                    ? "✅ Application withdrawn"
                                                    : "❌ Withdraw failed"
                                    );
                                }

                                case 7 -> js.viewNotifications(user.getId());

                                case 8 -> {
                                    System.out.println("🔒 Logged out");
                                    break;
                                }
                            }
                            if (ch == 8) break;
                        }
                    }

                    // =====================================================
                    // ================= EMPLOYER ==========================
                    // =====================================================
                    else {

                        EmployerService es = new EmployerService();

                        while (true) {
                            System.out.println("\n--- Employer Menu ---");
                            System.out.println("1. Create Company");
                            System.out.println("2. Post Job");
                            System.out.println("3. View My Jobs");
                            System.out.println("4. View Applicants");
                            System.out.println("5. Filter Applicants");
                            System.out.println("6. Shortlist Applicant");
                            System.out.println("7. Reject Applicant");
                            System.out.println("8. Bulk Shortlist");
                            System.out.println("9. Bulk Reject");
                            System.out.println("10. Add Comment");
                            System.out.println("11. Edit Job");
                            System.out.println("12. Close Job");
                            System.out.println("13. Reopen Job");
                            System.out.println("14. View Job Statistics");
                            System.out.println("15. Logout");

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

                                    System.out.println(
                                            es.createCompany(c)
                                                    ? "✅ Company created"
                                                    : "❌ Failed"
                                    );
                                }

                                case 2 -> {
                                    Job j = new Job();
                                    j.setEmployerId(user.getId());

                                    System.out.print("Title: ");
                                    j.setTitle(sc.nextLine());

                                    System.out.print("Company: ");
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

                                    System.out.println(
                                            es.postJob(j)
                                                    ? "✅ Job posted"
                                                    : "❌ Failed"
                                    );
                                }

                                case 3 -> es.viewMyJobs(user.getId());

                                case 4 -> {
                                    System.out.print("Job ID: ");
                                    es.viewApplicants(sc.nextInt());
                                    sc.nextLine();
                                }

                                case 5 -> {
                                    System.out.print("Job ID: ");
                                    int jobId = sc.nextInt();
                                    sc.nextLine();

                                    System.out.print("Skill: ");
                                    String skill = sc.nextLine();

                                    System.out.print("Min Experience: ");
                                    int e = sc.nextInt();
                                    sc.nextLine();

                                    System.out.print("Education: ");
                                    String edu = sc.nextLine();

                                    es.filterApplicants(jobId, skill,
                                            e <= 0 ? null : e, edu);
                                }

                                case 6 -> {
                                    System.out.print("Application ID: ");
                                    es.updateApplicationStatus(sc.nextInt(), "SHORTLISTED");
                                    sc.nextLine();
                                }

                                case 7 -> {
                                    System.out.print("Application ID: ");
                                    es.updateApplicationStatus(sc.nextInt(), "REJECTED");
                                    sc.nextLine();
                                }

                                case 8 -> {
                                    System.out.print("App IDs (1,2,3): ");
                                    es.bulkShortlist(parseIds(sc.nextLine()));
                                }

                                case 9 -> {
                                    System.out.print("App IDs (1,2,3): ");
                                    es.bulkReject(parseIds(sc.nextLine()));
                                }

                                case 10 -> {
                                    System.out.print("Application ID: ");
                                    int id = sc.nextInt();
                                    sc.nextLine();
                                    System.out.print("Comment: ");
                                    es.addComment(id, sc.nextLine());
                                }

                                case 11 -> {
                                    Job j = new Job();
                                    System.out.print("Job ID: ");
                                    j.setId(sc.nextInt());
                                    sc.nextLine();
                                    j.setEmployerId(user.getId());

                                    System.out.print("New Title: ");
                                    j.setTitle(sc.nextLine());

                                    System.out.print("Company: ");
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

                                    es.updateJob(j);
                                }

                                case 12 -> es.closeJob(sc.nextInt());
                                case 13 -> es.reopenJob(sc.nextInt());
                                case 14 -> es.viewJobStatistics(sc.nextInt());

                                case 15 -> {
                                    System.out.println("🔒 Logged out");
                                    break;
                                }
                            }
                            if (ch == 15) break;
                        }
                    }
                }

                case 3 -> System.exit(0);
            }
        }
    }

    private static List<Integer> parseIds(String input) {
        List<Integer> list = new ArrayList<>();
        for (String s : input.split(",")) {
            list.add(Integer.parseInt(s.trim()));
        }
        return list;
    }
}
