package com.revhire.dao;

import com.revhire.model.Application;
import java.util.List;

/**
 * Data access contract for application operations.
 */
public interface ApplicationDao {

    boolean applyJob(int userId, int jobId, String coverLetter);

    boolean alreadyApplied(int userId, int jobId);

    List<Application> getApplicationsByUser(int userId);

    List<Application> getApplicationsByJob(int jobId);

    boolean updateStatus(int applicationId, String status);

    boolean updateStatusBulk(List<Integer> applicationIds, String status);

    boolean addComment(int applicationId, String comment);

    int countByJobAndStatus(int jobId, String status);

    int getUserIdByApplication(int applicationId);

    boolean withdrawApplication(int appId, String reason);

    List<Application> filterApplicants(int jobId, String skill, Integer experience,
                                       String education, String dateFrom);
}
