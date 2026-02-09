package com.revhire.dao;

import com.revhire.model.Resume;

/**
 * Data access contract for resume operations.
 */
public interface ResumeDao {

    boolean saveResume(Resume resume);

    boolean updateResume(Resume resume);

    Resume getResumeByUserId(int userId);

    Resume getResumeByApplicationId(int applicationId);

    boolean hasResume(int userId);
}
