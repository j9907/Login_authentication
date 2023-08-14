package com.example.demo.Controller;

import java.util.Date;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.config.User.InMemoryUserRepository;
import com.example.demo.config.User.UserRepository;
import com.example.demo.config.jwt.JwtProperties;
import com.example.demo.config.model.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JwtController {

	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@PostMapping("/api/login")
	public String jwtCreate(@RequestBody Map<String,Object> data) {
		System.out.println("jwtCreact 실행");
		//System.out.println(data.get("username"));
		InMemoryUserRepository repo = new InMemoryUserRepository();
		
		
		String username = (String)data.get("username");
		String password = (String)data.get("password");
		User exisUser = repo.findByUsername(username,password);
		
		if(exisUser == null) {
			return "존재하지 않는 사용자";
		}
		
		String jwtToken = JWT.create()
				.withSubject(exisUser.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIRATION_TIME))
				.withClaim("id", exisUser.getId())
				.withClaim("username", exisUser.getUsername())
				.sign(Algorithm.HMAC512(JwtProperties.SCREAT));
		
		return jwtToken;

		
	}
	
}
