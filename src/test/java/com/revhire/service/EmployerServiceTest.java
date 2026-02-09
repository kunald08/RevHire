package com.revhire.service;

import com.revhire.model.Company;
import com.revhire.model.Job;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for EmployerService.
 * Requires a running MySQL instance with the revhire database.
 */
class EmployerServiceTest {

    private final EmployerService es = new EmployerService();

    @Test
    @DisplayName("Post a job with salary range and description")
    void postJob_success() {
        Job j = new Job();
        j.setEmployerId(1);
        j.setTitle("JUnit Tester Role");
        j.setDescription("Testing job posting via JUnit integration tests");
        j.setCompany("TestCorp");
        j.setLocation("Remote");
        j.setJobType("FULL_TIME");
        j.setExperience(2);
        j.setSkillsRequired("Java, JUnit, Maven");
        j.setEducationReq("B.Tech");
        j.setSalaryMin(400000);
        j.setSalaryMax(800000);

        assertTrue(es.postJob(j), "Job posting should succeed");
    }

    @Test
    @DisplayName("Close a non-existent job fails")
    void closeJob_invalidId_fails() {
        assertFalse(es.closeJob(-1), "Closing invalid job should fail");
    }

    @Test
    @DisplayName("Bulk shortlist with empty list returns zero")
    void bulkShortlist_emptyList_returnsZero() {
        assertEquals(0, es.bulkShortlist(List.of()),
                "Empty list should return 0 shortlisted");
    }

    @Test
    @DisplayName("Bulk reject with empty list returns zero")
    void bulkReject_emptyList_returnsZero() {
        assertEquals(0, es.bulkReject(List.of()),
                "Empty list should return 0 rejected");
    }

    @Test
    @DisplayName("Delete non-existent job fails")
    void deleteJob_invalidId_fails() {
        assertFalse(es.deleteJob(-1, 1),
                "Deleting non-existent job should fail");
    }

    @Test
    @DisplayName("Get company for non-existent employer returns null")
    void getCompany_noEmployer_returnsNull() {
        Company c = es.getCompany(-1);
        assertNull(c, "Non-existent employer should have no company");
    }

    @Test
    @DisplayName("Get job by invalid ID returns null")
    void getJobById_invalidId_returnsNull() {
        Job j = es.getJobById(-1);
        assertNull(j, "Non-existent job should return null");
    }

    @Test
    @DisplayName("Unread notification count for non-existent user is zero")
    void getUnreadNotificationCount_noUser_returnsZero() {
        assertEquals(0, es.getUnreadNotificationCount(-1),
                "Non-existent user should have 0 unread notifications");
    }
}
