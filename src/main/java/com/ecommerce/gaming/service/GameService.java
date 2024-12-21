package com.ecommerce.gaming.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.gaming.model.Game;

public interface GameService {

	Boolean existsByNameAndReleaseDate(String name, LocalDate releaseDate);

	void save(Game game);

	List<Game> getAllGames();
	
	List<Game> getAvailableGames();

	Game getById(Long id);

	String saveImage(MultipartFile image, String name) throws IOException;

	byte[] getImage(String imagepath) throws IOException;
	
	Game getGameByName(String name);

}
