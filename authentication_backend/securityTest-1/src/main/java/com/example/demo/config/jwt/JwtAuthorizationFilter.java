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

// 인가
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
		
		// 토큰 검증 ( 인증이라 authenticationManager도 필요 없다)
		// SecurityContext에 직접 접근해서 세션을 만들때 자동으로 userDetilsService에 있는
		// loadByUsername이 호출됨
		//System.out.println("Token : " + token);
		
		String username = JWT.require(Algorithm.HMAC512(JwtProperties.SCREAT)).build().verify(token)
						.getClaim("username").asString();
		//System.out.println("username : " + username);
		if(username != null) {
			User user = repo.findByUsername(username);
			
			 // 인증은 토큰 검증 시 끝남 = 인증을 하기 위해서가 아닌 스프링 시큐리티가 수행해주는 권한 처리를 위해
			// 아래와 같이 토큰을 만들어서 Authentication 객체를 강제로 만들고 그걸 세션에 저장
			
			PrincipalDetails principalDetailis = new PrincipalDetails(user);
			Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetailis, // 나중에 컨트롤러에서 DI 
					null, // 패스워드는 모르니까 null처리 , 지금 인증하는게 아니기 때문에
					principalDetailis.getAuthorities());
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
		}
		
		chain.doFilter(request, response);
		
		
		super.doFilterInternal(request, response, chain);
	}
}
