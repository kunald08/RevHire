package com.revhire.app;

import com.revhire.model.User;
import com.revhire.service.AuthService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        AuthService authService = new AuthService();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n*** RevHire Job Portal ***");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter ur choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
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

                    if (authService.register(u)) {
                        System.out.println("✅ Registration successful");
                    } else {
                        System.out.println("❌ Registration failed");
                    }
                }

                case 2 -> {
                    System.out.print("Email: ");
                    String email = sc.nextLine();
                    System.out.print("Password: ");
                    String pwd = sc.nextLine();

                    User user = authService.login(email, pwd);
                    if (user != null) {
                        System.out.println("✅ Welcome " + user.getName()
                                + " (" + user.getRole() + ")");
                    } else {
                        System.out.println("❌ Invalid credentials");
                    }
                }

                case 3 -> System.exit(0);
            }
        }
    }
}
