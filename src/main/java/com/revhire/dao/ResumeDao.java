package com.revhire.dao;

import com.revhire.model.Resume;

public interface ResumeDao {
    boolean saveResume(Resume resume);
    Resume getResumeByUserId(int userId);
}
