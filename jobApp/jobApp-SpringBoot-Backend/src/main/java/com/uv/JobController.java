package com.uv;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.uv.model.JobPost;
import com.uv.service.JobService;

@RestController
@CrossOrigin(origins = "*")
public class JobController {
	
	@Autowired
	JobService service;
	
	@GetMapping("jobPosts")
	public List<JobPost> getAllJobs() {
		return service.getAllJobs();
	}
	
	@GetMapping("jobPost/{postId}")
	public JobPost getJob(@PathVariable int postId) {
		return service.getJob(postId);
	}
	
	@GetMapping("jobPosts/keyword/{keyword}")
	public List<JobPost> searchByKeyword(@PathVariable String keyword){
		return service.search(keyword);
	}
	
	@PostMapping("jobPost")
	public void addJob(@RequestBody JobPost job) {
		service.addJob(job);
	}
	
	@PutMapping("jobPost")
	public JobPost updateJob(@RequestBody JobPost job) {
		service.updateJob(job);
		return service.getJob(job.getPostId());
	}
	
	@DeleteMapping("jobPost/{postId}")
	public String deleteJob(@PathVariable int postId) {
		service.deleteJob(postId);
		return "Job Deleted";
	}
	
	@GetMapping("load")
	public String loadData() {
		service.load();
		return "success";
	}
}