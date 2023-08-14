package com.example.demo.config.jwt;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.config.auth.PrincipalDetails;
import com.example.demo.dto.LoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	
	private final AuthenticationManager authenticationManager;
	
	
	// Authentication 객체를 만들어서 리턴 => AuthentiationManager 의존
	// 인증 요청시에 실행되는 함수 => /login
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		System.out.println("JwtAuthenticationFilter : 진입");
		
		//request에 잇는 username과 password를 파싱해서 자바 object로 받음
		ObjectMapper om = new ObjectMapper();
		LoginRequestDto loginRequestDto = null;
		try {
			loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("JwtAutenticationFilter: " + loginRequestDto);
		
		// 패스워드 토큰 생성
		UsernamePasswordAuthenticationToken authenticationToken = 
					new UsernamePasswordAuthenticationToken(
							loginRequestDto.getPassword(),
							loginRequestDto.getPassword());
		
		
		System.out.println("JwtAuthenticationFilter : 토큰생성완료");
		
		// authenticate() 함수가 호출되면 인증 프로바이더가 유지 디테일 서비스의
		// loadUserByusername (첫번째 토큰 파라미터)를 호출하고
		// UserDetails를 리턴받아서 토큰의 두번째 파라미터 (credential)과 
		// UserDetails의 getPassword()함수를 비교해서 동일하면
		// Authentication 객체를 만들어서 필터체인으로 리턴해준다.
		
		// 인증 프로바이더의 디폴트 서비스는 UserDetailsService 타입
		// 인증 프로바이더의 디폴트 암호화 방시은 BCryptPassswordEncoder
		// 인증 프로바이더에게 알려줄 필요가 없음
		
		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		PrincipalDetails principalDetailis = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("Authenticaiton :" + principalDetailis.getUser().getUsername());
		return authentication;
	}
	
	// JWT Token 생성해서 response에 담아주기
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
			PrincipalDetails principalDetailis = (PrincipalDetails) authResult.getPrincipal();
			
			String jwtToken = JWT.create()
								.withSubject(principalDetailis.getUsername())
								.withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIRATION_TIME))
								.withClaim("id",principalDetailis.getUser().getId())
								.withClaim("username",principalDetailis.getUser().getUsername())
								.sign(Algorithm.HMAC512(JwtProperties.SCREAT));
			
			response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
		
	}

}
