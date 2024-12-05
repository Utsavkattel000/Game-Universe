package com.ecommerce.gaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.gaming.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	User getByEmail(String Email);
	
	User getByPhone(String Phone);

}
