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
	
	
	// Authentication ��ü�� ���� ���� => AuthentiationManager ����
	// ���� ��û�ÿ� ����Ǵ� �Լ� => /login
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		System.out.println("JwtAuthenticationFilter : ����");
		
		//request�� �մ� username�� password�� �Ľ��ؼ� �ڹ� object�� ����
		ObjectMapper om = new ObjectMapper();
		LoginRequestDto loginRequestDto = null;
		try {
			loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("JwtAutenticationFilter: " + loginRequestDto);
		
		// �н����� ��ū ����
		UsernamePasswordAuthenticationToken authenticationToken = 
					new UsernamePasswordAuthenticationToken(
							loginRequestDto.getPassword(),
							loginRequestDto.getPassword());
		
		
		System.out.println("JwtAuthenticationFilter : ��ū�����Ϸ�");
		
		// authenticate() �Լ��� ȣ��Ǹ� ���� ���ι��̴��� ���� ������ ������
		// loadUserByusername (ù��° ��ū �Ķ����)�� ȣ���ϰ�
		// UserDetails�� ���Ϲ޾Ƽ� ��ū�� �ι�° �Ķ���� (credential)�� 
		// UserDetails�� getPassword()�Լ��� ���ؼ� �����ϸ�
		// Authentication ��ü�� ���� ����ü������ �������ش�.
		
		// ���� ���ι��̴��� ����Ʈ ���񽺴� UserDetailsService Ÿ��
		// ���� ���ι��̴��� ����Ʈ ��ȣȭ ����� BCryptPassswordEncoder
		// ���� ���ι��̴����� �˷��� �ʿ䰡 ����
		
		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		PrincipalDetails principalDetailis = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("Authenticaiton :" + principalDetailis.getUser().getUsername());
		return authentication;
	}
	
	// JWT Token �����ؼ� response�� ����ֱ�
	
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
