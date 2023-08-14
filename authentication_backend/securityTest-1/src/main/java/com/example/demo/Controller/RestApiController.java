package com.example.demo.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.User.UserRepository;
import com.example.demo.config.auth.PrincipalDetails;
import com.example.demo.config.model.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RestApiController {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("home")
	public String home() {
		return "<h1>home</h1>";
	}
	
	// Jwt�� ����ϸ� UserDetailsServicce�� ȣ������ �ʱ� ������ @AuthenticationPrincipal ��� �Ұ���
	// �ֳ��ϸ� @AuthenticationPrincipal�� UserDetailsService�� ���ϵ� �� ��������� ������
	
	@GetMapping("/user")
	public Authentication user(Authentication authentication) {
		//System.out.println("auth : "+ authentication);
		
		if(authentication != null) {
		PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("principal : "+principal.getUser().getId());
		System.out.println("principal : "+principal.getUser().getUsername());
		System.out.println("principal : "+principal.getUser().getPassword());
		System.out.println("principal : "+principal.getUser().getRoles());
		}
		return authentication;
	}
	
	@PostMapping("join")
	public String join(@RequestBody User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setRoles("ROLE_USER");
		userRepository.save(user);
		return "ȸ�����ԿϷ�";
	}
}
