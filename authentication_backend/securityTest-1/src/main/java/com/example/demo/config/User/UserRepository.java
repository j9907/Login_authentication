package com.example.demo.config.User;

import org.springframework.stereotype.Repository;

import com.example.demo.config.model.User;

@Repository
public interface UserRepository {
	
	//User findByUsername(String username);
	void save(User user);
	User findByUsername(String username, String password);
	User findByUsername(String username);
}
