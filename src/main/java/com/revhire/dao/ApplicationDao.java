package com.revhire.dao;

import com.revhire.model.Application;
import java.util.List;

public interface ApplicationDao {

    boolean applyJob(int userId, int jobId);

    boolean alreadyApplied(int userId, int jobId);

    List<Application> getApplicationsByUser(int userId);

    boolean withdrawApplication(int appId);
}
