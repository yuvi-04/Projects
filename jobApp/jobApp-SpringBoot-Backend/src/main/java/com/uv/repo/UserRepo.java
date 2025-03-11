package com.uv.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uv.model.User;

public interface UserRepo extends JpaRepository<User, Integer> {
	User findByUsername(String username);
}
