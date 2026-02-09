package com.revhire.service;

import com.revhire.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AuthService.
 * Requires a running MySQL instance with the revhire database.
 */
class AuthServiceTest {

    private final AuthService authService = new AuthService();

    @Test
    @DisplayName("Register a new Job Seeker with profile fields")
    void register_jobSeeker_success() {
        User u = new User();
        u.setName("JUnit Seeker");
        u.setEmail("junit_seeker_" + System.currentTimeMillis() + "@test.com");
        u.setPassword("pass123");
        u.setRole("JOB_SEEKER");
        u.setPhone("9876543210");
        u.setEducation("B.Tech");
        u.setSkills("Java, SQL");
        u.setSecurityQuestion("Favourite color?");
        u.setSecurityAnswer("blue");

        assertTrue(authService.register(u), "Registration should succeed");
    }

    @Test
    @DisplayName("Register a new Employer")
    void register_employer_success() {
        User u = new User();
        u.setName("JUnit Employer");
        u.setEmail("junit_emp_" + System.currentTimeMillis() + "@test.com");
        u.setPassword("pass123");
        u.setRole("EMPLOYER");
        u.setSecurityQuestion("Favourite pet?");
        u.setSecurityAnswer("cat");

        assertTrue(authService.register(u), "Employer registration should succeed");
    }

    @Test
    @DisplayName("Login with invalid credentials returns null")
    void login_invalidCredentials_returnsNull() {
        User user = authService.login("nonexistent@test.com", "wrong");
        assertNull(user, "Invalid login should return null");
    }

    @Test
    @DisplayName("Change password fails with wrong old password")
    void changePassword_wrongOld_fails() {
        assertFalse(authService.changePassword(1, "wrongOld", "newPass"),
                "Should fail when old password doesn't match");
    }

    @Test
    @DisplayName("Get user by invalid ID returns null")
    void getUserById_invalidId_returnsNull() {
        User u = authService.getUserById(-1);
        assertNull(u, "Non-existent user should return null");
    }

    @Test
    @DisplayName("Profile completion calculates correctly")
    void profileCompletion_partialProfile() {
        User u = new User();
        u.setPhone("1234567890");
        u.setEducation("B.Tech");
        // workExperience, skills, certifications are null
        assertEquals(40, u.getProfileCompletion(),
                "2 out of 5 fields filled = 40%");
    }

    @Test
    @DisplayName("Profile completion 100% when all fields set")
    void profileCompletion_fullProfile() {
        User u = new User();
        u.setPhone("1234567890");
        u.setEducation("B.Tech");
        u.setWorkExperience("2 years at TCS");
        u.setSkills("Java, SQL, Spring");
        u.setCertifications("AWS Certified");
        assertEquals(100, u.getProfileCompletion(),
                "All 5 fields filled = 100%");
    }
}
