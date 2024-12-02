package com.cos.security1.config.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

// 구글로 부터 받은 userRequest 데이터에 대한 후처리
//함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{
	
//	@Autowired
//	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println(userRequest);
		
		OAuth2User oauth2User = super.loadUser(userRequest);
		
		String provider = userRequest.getClientRegistration().getClientId(); // google
		String providerId = oauth2User.getAttribute("sub");		// Id
		String username = provider+"_"+providerId;	// google_102345679...
		String password = "0";
		String email = oauth2User.getAttribute("email");
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		// Oauth 로그인시 강제 회원가입 진행		
		if(userEntity == null) {
			System.out.println("구글 로그인이 최초입니다.");
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity);
		}
		else {
			System.out.println("구글 로그인을 이미 한적 있습니다.");
		}
		
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
}
