package com.smartcontact.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smartcontact.dao.UserRepository;
import com.smartcontact.entites.User;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {

		User user = userRepository.getUserByUserName(username);

		if (user == null) {
			throw new UsernameNotFoundException("Could not found User !!");
		}

		CustomUserDetails customUserDetail = new CustomUserDetails(user);

		return customUserDetail;
	}

}
