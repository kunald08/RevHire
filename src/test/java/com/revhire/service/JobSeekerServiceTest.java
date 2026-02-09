package com.revhire.service;

import com.revhire.model.Resume;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for JobSeekerService.
 * Requires a running MySQL instance with the revhire database.
 */
class JobSeekerServiceTest {

    private final JobSeekerService js = new JobSeekerService();

    @Test
    @DisplayName("Create a resume with projects section successfully")
    void createResume_success() {
        Resume r = new Resume();
        r.setUserId(1);
        r.setObjective("Backend Developer");
        r.setSkills("Java, JDBC, SQL");
        r.setExperience("2 Years");
        r.setEducation("B.Tech");
        r.setProjects("RevHire Portal, E-Commerce App");

        assertTrue(js.createResume(r), "Resume creation should succeed");
    }

    @Test
    @DisplayName("Apply without resume is blocked")
    void applyJob_withoutResume_fails() {
        assertFalse(js.applyJob(9999, 1, null),
                "Apply should fail when user has no resume");
    }

    @Test
    @DisplayName("Apply with cover letter (without resume) is blocked")
    void applyJob_withCover_withoutResume_fails() {
        assertFalse(js.applyJob(9999, 1, "I am very interested in this role."),
                "Apply should fail when user has no resume even with cover letter");
    }

    @Test
    @DisplayName("Withdraw invalid application fails")
    void withdraw_invalidId_fails() {
        assertFalse(js.withdraw(-1, "Changed my mind"),
                "Withdraw should fail for non-existent application");
    }

    @Test
    @DisplayName("Withdraw without reason still works for invalid application")
    void withdraw_invalidId_noReason_fails() {
        assertFalse(js.withdraw(-1, null),
                "Withdraw should fail for non-existent application");
    }

    @Test
    @DisplayName("Search jobs with no filters returns results without error")
    void searchJobs_noFilters_noError() {
        assertDoesNotThrow(() -> js.searchJobs(null, null, null, null, null, null, null),
                "Search with no filters should not throw");
    }

    @Test
    @DisplayName("Unread notification count for non-existent user is zero")
    void getUnreadNotificationCount_noUser_returnsZero() {
        assertEquals(0, js.getUnreadNotificationCount(-1),
                "Non-existent user should have 0 unread notifications");
    }
}
