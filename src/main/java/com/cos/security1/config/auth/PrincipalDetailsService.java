package com.cos.security1.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

// 시큐리티 설정에서 loginProcessingUrl으로 login 요청이 오면
// 자동으로 UserDetailsService 타입으로 IOC 되어있는 loadUserByUsername 함수가 실행됨

@Service
public class PrincipalDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    User userEntity = userRepository.findByUsername(username);
	    if (userEntity == null) {
	        System.out.println("user 찾기 실패: " + username);
	        throw new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다: " + username);
	    }
	    System.out.println("user 찾기 성공: " + username);
	    return new PrincipalDetails(userEntity);
	}
	
}
