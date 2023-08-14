package com.example.demo.config.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.example.demo.config.model.User;

@Repository
public class InMemoryUserRepository implements UserRepository{

	private List<User> userMap = new ArrayList<>();
	
	public InMemoryUserRepository() {
		User defaultuser = new User(1L, "test", "passwd", "default@example.com", "ROLE_USER", "1234", "local");
		User defaultadmin = new User(1L, "admin","pw", "admin@example.com","ROLE_ADMIN","123","local");
		userMap.add(defaultuser);
		userMap.add(defaultadmin);
	}
	
	@Override
	public User findByUsername(String username, String password) {
		return userMap.stream()
                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
	}

	@Override
	public void save(User user) {
		userMap.add(user);
		
	}

	@Override
	public User findByUsername(String username) {
		return userMap.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
	}



}
