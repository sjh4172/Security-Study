package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Controller
public class IndexController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/test/login")
	public @ResponseBody String loginTest(
			Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) {
		PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
		System.out.println("authentication: " + principalDetails.getUser());
		System.out.println("userDetails " + userDetails.getUser());
		return "세션 정보 확인하기";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOAuthLogin(
			Authentication authentication, 
			@AuthenticationPrincipal OAuth2User oauth) {
		OAuth2User principalDetails = (OAuth2User)authentication.getPrincipal();
		System.out.println("authentication: " + principalDetails.getAttributes());
		System.out.println("oauth2User: " + oauth.getAttributes());
		return "OAuth 세션 정보 확인하기";
	}
	

	// Oauth 로그인과 일반 로그인 모두 PrincipalDetails로 처리 할 수 있음
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println(principalDetails.getUser());
		return "user";
	}

	
	@GetMapping({"", "/"})
	public String index() {
		return "index";
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}

	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(User user) {
		System.out.println(user);
		user.setRole("ROLE_USER");
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userRepository.save(user);
		return "redirect:/loginForm";
	}

	//@Secured("ROLE_ADMIN") // 특정 함수에만 접근제한을 걸때 사용 
	@PreAuthorize("hasRole('ROLE_ADMIN')") 
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "데이터 정보";
	}
}
