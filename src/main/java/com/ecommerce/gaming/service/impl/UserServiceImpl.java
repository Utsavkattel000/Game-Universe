package com.ecommerce.gaming.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.gaming.model.User;
import com.ecommerce.gaming.repository.UserRepository;
import com.ecommerce.gaming.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	UserRepository userRepo;
	
	@Override
	public void save(User user) {
		userRepo.save(user);
	}

	@Override
	public User getByEmail(String email) {
		
		return userRepo.getByEmail(email);
	}

	@Override
	public User getByPhone(String phone) {
		
		return userRepo.getByPhone(phone);
	}
	@Override
	public boolean verifyPassword(String password, User user, BCryptPasswordEncoder encoder) {
		if (user.getPassword() != null && encoder.matches(password, user.getPassword())) {
			return true;
		}
		return false;
	}

}
