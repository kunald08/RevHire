package com.revhire.app;

import com.revhire.model.User;
import com.revhire.model.Resume;
import com.revhire.service.AuthService;
import com.revhire.service.JobSeekerService;

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
            sc.nextLine(); // consume newline

            switch (choice) {

                // ================= REGISTER =================
                case 1 -> {
                    User u = new User();

                    System.out.print("Enter Name: ");
                    u.setName(sc.nextLine());

                    System.out.print("Enter Email: ");
                    u.setEmail(sc.nextLine());

                    System.out.print("Enter Password: ");
                    u.setPassword(sc.nextLine());

                    System.out.print("Enter Role (JOB_SEEKER / EMPLOYER): ");
                    u.setRole(sc.nextLine());

                    if (authService.register(u)) {
                        System.out.println("✅ Registration Successful");
                    } else {
                        System.out.println("❌ Registration Failed");
                    }
                }

                // ================= LOGIN =================
                case 2 -> {
                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();

                    System.out.print("Enter Password: ");
                    String password = sc.nextLine();

                    User user = authService.login(email, password);

                    if (user == null) {
                        System.out.println("❌ Invalid Credentials");
                        break;
                    }

                    System.out.println("✅ Welcome " + user.getName()
                            + " (" + user.getRole() + ")");

                    // ================= JOB SEEKER MENU =================
                    if (user.getRole().equalsIgnoreCase("JOB_SEEKER")) {

                        JobSeekerService jsService = new JobSeekerService();

                        while (true) {
                            System.out.println("\n--- Job Seeker Menu ---");
                            System.out.println("1. View Jobs");
                            System.out.println("2. Create Resume");
                            System.out.println("3. Apply for Job");
                            System.out.println("4. View Applications");
                            System.out.println("5. Withdraw Application");
                            System.out.println("6. Logout");

                            int ch = sc.nextInt();
                            sc.nextLine(); // consume newline

                            switch (ch) {

                                case 1 -> jsService.viewJobs();

                                case 2 -> {
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

                                    if (jsService.createResume(r)) {
                                        System.out.println("✅ Resume Created");
                                    } else {
                                        System.out.println("❌ Resume Creation Failed");
                                    }
                                }

                                case 3 -> {
                                    System.out.print("Enter Job ID: ");
                                    int jobId = sc.nextInt();

                                    if (jsService.applyJob(user.getId(), jobId)) {
                                        System.out.println("✅ Applied Successfully");
                                    } else {
                                        System.out.println("❌ Application Failed");
                                    }
                                }

                                case 4 -> jsService.viewApplications(user.getId());

                                case 5 -> {
                                    System.out.print("Enter Application ID: ");
                                    int appId = sc.nextInt();

                                    if (jsService.withdraw(appId)) {
                                        System.out.println("✅ Application Withdrawn");
                                    } else {
                                        System.out.println("❌ Withdraw Failed");
                                    }
                                }

                                case 6 -> {
                                    System.out.println("🔒 Logged out");
                                    break;
                                }

                                default -> System.out.println("❌ Invalid Choice");
                            }

                            if (ch == 6) break;
                        }
                    }

                    // ================= EMPLOYER (PHASE 3 PLACEHOLDER) =================
                    else if (user.getRole().equalsIgnoreCase("EMPLOYER")) {
                        System.out.println("⚠ Employer module will be available soon...");
                    }
                }

                // ================= EXIT =================
                case 3 -> {
                    System.out.println("👋 Thank you for using RevHire");
                    System.exit(0);
                }

                default -> System.out.println("❌ Invalid Option");
            }
        }
    }
}
