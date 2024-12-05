package com.ecommerce.gaming.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.gaming.model.Game;
import com.ecommerce.gaming.repository.GameRepository;
import com.ecommerce.gaming.service.GameService;

@Service
public class GameServiceImpl implements GameService{

	@Autowired
	GameRepository gamerepo;
	
	 private static final String STORAGE_PATH = "C:\\Users\\utsav\\OneDrive\\Documents\\workspace-spring-tool-suite-4-4.18.0.RELEASE\\GameSelling\\src\\main\\resources\\static\\images";
	
	@Override
	public Boolean existsByNameAndReleaseDate(String name, LocalDate releaseDate) {
		return gamerepo.existsByNameAndReleaseDate(name, releaseDate);
	}

	@Override
	public void save(Game game) {
		gamerepo.save(game);
	}

	@Override
	public List<Game> getAllGames() {
		
		return gamerepo.findAll();
	}

	@Override
	public Game getById(Long id) {
		
		return gamerepo.getReferenceById(id);
	}

	@Override
	public String saveImage(MultipartFile image, String name) throws IOException {
	    String safeProductName = name.replaceAll("[^a-zA-Z0-9]", "_");
	    String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.'));
	    String fileName = safeProductName + "_" + UUID.randomUUID().toString() + extension;
	    Path imagePath = Paths.get(STORAGE_PATH, fileName);
	    Files.createDirectories(imagePath.getParent());
	    Files.write(imagePath, image.getBytes());
	    return "images/" + fileName;
	}



	@Override
	public byte[] getImage(String imagepath) throws IOException {
		Path filePath = Paths.get(STORAGE_PATH, imagepath);
        return Files.readAllBytes(filePath);
	}
}
