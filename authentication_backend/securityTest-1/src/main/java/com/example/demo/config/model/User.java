package com.example.demo.config.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {

	private long id;
	private String username;
	private String password;
	private String email;
	private String roles;
	private String providerId;
	private String provider;
	
	public List<String> getRoleList(){
	    if (this.roles != null && !this.roles.isEmpty()) {
	        //.out.println(this.roles);
	        return Arrays.asList(this.roles.split(","));
	    }
	    return new ArrayList<>();
	}
	
}
