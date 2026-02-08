package com.revhire.dao;

import com.revhire.model.Job;
import java.util.List;

public interface JobDao {

    List<Job> getAllJobs();

    List<Job> searchJobs(
            String title,
            String location,
            String jobType,
            Integer minExperience
    );

    boolean createJob(Job job);
    List<Job> getJobsByEmployer(int employerId);

    boolean updateJob(Job job);

    boolean updateJobStatus(int jobId, String status);

}
