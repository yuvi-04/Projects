package com.uv.service;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uv.model.JobPost;
import com.uv.repo.JobRepo;

@Service
public class JobService {
	
	@Autowired
	JobRepo repo;
	
	public void addJob(JobPost jobPost) {
		repo.save(jobPost);
	}
	
	public List<JobPost> getAllJobs() {
		return repo.findAll();
	}

	public JobPost getJob(int postId) {
		return repo.findById(postId).get();
	}

	public void updateJob(JobPost job) {
		repo.save(job);
	}

	public void deleteJob(int postId) {
		repo.deleteById(postId);
	}

	public void load() {
		List<JobPost> jobs = new ArrayList<>(Arrays.asList(
				new JobPost(1, "Java Developer", "Good in Development", 3, List.of("Core Java", "J2EE")),
				new JobPost(2, "MERN Developer", "Good Knowledge of Nodejs and frameworks", 2,
						List.of("MongoDB", "React", "ExpressJS")),
				new JobPost(3, "IOS Developer", "Knowledge of mobile Development", 1,
						List.of("Objective-C", "IOS architecture", "IOS fundamentals"))
			));
		repo.saveAll(jobs);
	}

	public List<JobPost> search(String keyword) {
		// TODO Auto-generated method stub
		return repo.findByPostProfileContainingOrPostDescContaining(keyword, keyword);
	}
}
