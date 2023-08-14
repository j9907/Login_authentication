package com.example.demo.config.jwt;

public interface JwtProperties {
	String SCREAT = "겟인데어"; // 우리 서버만 알고있는 비밀값
	int EXPIRATION_TIME = 864000000;
	String TOKEN_PREFIX = "Bearer ";
	String HEADER_STRING= "Authorization";
	
}
