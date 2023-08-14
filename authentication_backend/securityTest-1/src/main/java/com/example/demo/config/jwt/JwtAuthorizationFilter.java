package com.example.demo.config.jwt;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.config.User.InMemoryUserRepository;
import com.example.demo.config.User.UserRepository;
import com.example.demo.config.auth.PrincipalDetails;
import com.example.demo.config.model.User;

// �ΰ�
public class JwtAuthorizationFilter extends BasicAuthenticationFilter  {

	private InMemoryUserRepository repo;
	
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, InMemoryUserRepository userRepository) {
		super(authenticationManager);
		this.repo = userRepository;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String header = request.getHeader(JwtProperties.HEADER_STRING);
		System.out.println("header Authorization :" + header);
		
		if(header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
			chain.doFilter(request, response);
			return;
		}
		
		String token = request.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX, "");
		
		// ��ū ���� ( �����̶� authenticationManager�� �ʿ� ����)
		// SecurityContext�� ���� �����ؼ� ������ ���鶧 �ڵ����� userDetilsService�� �ִ�
		// loadByUsername�� ȣ���
		//System.out.println("Token : " + token);
		
		String username = JWT.require(Algorithm.HMAC512(JwtProperties.SCREAT)).build().verify(token)
						.getClaim("username").asString();
		//System.out.println("username : " + username);
		if(username != null) {
			User user = repo.findByUsername(username);
			
			 // ������ ��ū ���� �� ���� = ������ �ϱ� ���ؼ��� �ƴ� ������ ��ť��Ƽ�� �������ִ� ���� ó���� ����
			// �Ʒ��� ���� ��ū�� ���� Authentication ��ü�� ������ ����� �װ� ���ǿ� ����
			
			PrincipalDetails principalDetailis = new PrincipalDetails(user);
			Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetailis, // ���߿� ��Ʈ�ѷ����� DI 
					null, // �н������ �𸣴ϱ� nulló�� , ���� �����ϴ°� �ƴϱ� ������
					principalDetailis.getAuthorities());
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
		}
		
		chain.doFilter(request, response);
		
		
		super.doFilterInternal(request, response, chain);
	}
}
