package com.ecommerce.gaming.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ecommerce.gaming.model.User;

public interface UserService {

	void save(User user);
	
	User getByEmail(String email);
	
	User getByPhone(String phone);

	boolean verifyPassword(String password, User user, BCryptPasswordEncoder encoder);
	
}
