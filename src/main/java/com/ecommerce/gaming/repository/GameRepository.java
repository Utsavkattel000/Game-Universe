package com.ecommerce.gaming.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.gaming.model.Game;

public interface GameRepository extends  JpaRepository<Game, Long>{

	Boolean existsByNameAndReleaseDate(String name,LocalDate releaseDate);
	
}
