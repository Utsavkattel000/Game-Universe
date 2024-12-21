package com.ecommerce.gaming.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.gaming.model.Game;
import com.ecommerce.gaming.model.RedeemCodes;
import com.ecommerce.gaming.model.User;
import com.ecommerce.gaming.service.GameService;
import com.ecommerce.gaming.service.RedeemCodesService;
import com.ecommerce.gaming.service.UserService;
import com.ecommerce.gaming.utils.MailUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
public class GameController {

	@Autowired
	GameService gameService;

	@Autowired
	RedeemCodesService redeemCodeService;

	@Autowired
	UserService userService;;

	@Autowired
	MailUtils mailUtil;

	@GetMapping("/newGame")
	public String newGame(RedirectAttributes attribute, HttpSession session) {
		if (session.getAttribute("admin") == null) {
			attribute.addFlashAttribute("error", "Please login");
			return "redirect:/login";
		}
        
		return "newGame";
	}
    @Transactional
	@PostMapping("/newGame")
	public String postGame(RedirectAttributes attribute, HttpSession session, @ModelAttribute Game game,
			@RequestParam("thumbnail") MultipartFile image) {
		if (session.getAttribute("admin") == null) {
			attribute.addFlashAttribute("error", "Please login");
			return "redirect:/login";
		}

		try {
			String imagePath = null;
			try {
				imagePath = gameService.saveImage(image, game.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			game.setImagePath(imagePath);
			gameService.save(game);
			attribute.addFlashAttribute("success", "Game added successfully. Please add codes for it.");
			return "redirect:/adminDashboard";
		} catch (Exception e) {
			e.printStackTrace();
			attribute.addFlashAttribute("error", "Game failed to add");
			return "redirect:/newGame";
		}
	}

	@GetMapping("/gamesForCodes")
	public String gamesForCodes(RedirectAttributes attribute, HttpSession session, Model model) {
		if (session.getAttribute("admin") == null) {
			attribute.addFlashAttribute("error", "Please login");
			return "redirect:/login";
		}
		List<Game> games = gameService.getAllGames();
		List<List<Game>> partitionedGames = partitionList(games, 3);
		model.addAttribute("partitionedGames", partitionedGames);
		return "gamesforcodes";
	}

	private List<List<Game>> partitionList(List<Game> list, int size) {
		List<List<Game>> partitions = new ArrayList<>();
		for (int i = 0; i < list.size(); i += size) {
			partitions.add(list.subList(i, Math.min(i + size, list.size())));
		}
		return partitions;
	}

	@GetMapping("/addcode")
	public String newCodes(HttpSession session, RedirectAttributes attribute, @RequestParam Long id, Model model) {
		if (session.getAttribute("admin") == null) {
			attribute.addFlashAttribute("error", "Please login");
			return "redirect:/login";
		}
		Game game = gameService.getById(id);
		model.addAttribute("game", game);
		return "addCode";
	}
	
    @Transactional
	@PostMapping("/addcodefinal")
	public String submitGameCodes(@RequestParam("gameid") Long gameId, @RequestParam("numberofcodes") int numberOfCodes,
			@RequestParam("codes") List<String> codes, Model model, HttpSession session, RedirectAttributes attribute) {
		Game game = gameService.getById(gameId);
		List<RedeemCodes> testcodes = game.getRedeemCodes();

		if (session.getAttribute("admin") == null) {
			attribute.addFlashAttribute("error", "Please login");
			return "redirect:/login";
		}
		if (codes == null || codes.size() < numberOfCodes) {
			model.addAttribute("error", "Number of codes provided is less than the expected number.");
			return "redirect:/addcode";
		}
		for (String codess : codes) {
			for (RedeemCodes testcode : testcodes) {
				if (testcode.getCode().equals(codess)) {
					attribute.addFlashAttribute("error", codess + " Already exists.");
					return "redirect:/addcode";
				}
			}
		}
		for (int i = 0; i < numberOfCodes; i++) {
			RedeemCodes redeemCode = new RedeemCodes();
			redeemCode.setGame(game);
			redeemCode.setCode(codes.get(i));
			redeemCode.setStatus("new");
			redeemCodeService.save(redeemCode);
		}

		attribute.addFlashAttribute("success", "Codes added successfully.");
		return "redirect:/adminDashboard";
	}

	@GetMapping("/exploregames")
	public String exploreGames(Model model, HttpSession session) {
		List<Game> games = gameService.getAvailableGames();
		session.removeAttribute("purchaseattemptgame");
		List<List<Game>> partitionedGames = partitionList(games, 3);
		model.addAttribute("partitionedGames", partitionedGames);
		return "exploreGames";
	}

	@GetMapping("/gameinfo")
	public String buygame(Model model, @RequestParam Long id,HttpSession session) {
		session.removeAttribute("purchaseattemptgame");
		Game game = gameService.getById(id);
		model.addAttribute("game", game);
		return "gameinfo";
	}

	@GetMapping("/buy")
	public String buy(@RequestParam Long id, HttpSession session, RedirectAttributes attribute) {
		session.setAttribute("purchaseattemptgame", id);
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before buying");
			return "redirect:/login";
		}
		return "redirect:/buyerinfo";
	}

	@GetMapping("/buyerinfo")
	public String buyerinfo(HttpSession session, RedirectAttributes attribute) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before buying");
			return "redirect:/login";
		}
		return "buyerinfo";
	}

	@PostMapping("/buyerinfo")
	public String postbuyer(@RequestParam String email, @RequestParam String paymentmethod, HttpSession session,
			RedirectAttributes attribute) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before buying");
			return "redirect:/login";
		}

		session.setAttribute("buyeremail", email);
		if (paymentmethod.equals("esewa")) {
			return "redirect:/esewa";
		}
		return "redirect:/imepay";
	}

	@GetMapping("/esewa")
	public String esewa(HttpSession session, RedirectAttributes attribute) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before buying");
			return "redirect:/login";
		}
		return "esewa";
	}

	@GetMapping("/imepay")
	public String imepay(HttpSession session, RedirectAttributes attribute) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before buying");
			return "redirect:/login";
		}
		return "imepay";
	}

	@PostMapping("/payment")
	public String payment(@RequestParam String id, HttpSession session) {
		Long gameid = (Long) session.getAttribute("purchaseattemptgame");
		Game game = gameService.getById(gameid);
		Random random = new Random();
		int randomNumber = random.nextInt(900000) + 100000;
		String otp = Integer.toString(randomNumber);
		session.setAttribute("otp", otp);
		mailUtil.sendEmail(id, "Your OTP for Rs." + game.getPrice() + " is " + otp, "OTP. Do not share.");
		return "redirect:/otp";
	}

	@GetMapping("/otp")
	public String otp(HttpSession session, RedirectAttributes attribute) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before buying");
			return "redirect:/login";
		}
		return "otp";
	}
     @Transactional
	@PostMapping("/otp")
	public String otpPost(HttpSession session, RedirectAttributes attribute, @RequestParam String otp) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before buying");
			return "redirect:/login";
		}
		String otpfromsession = (String) session.getAttribute("otp");
		if (otpfromsession.equals(otp)) {
			Long gameId = (Long) session.getAttribute("purchaseattemptgame");
			sendCode(gameId, (String) session.getAttribute("buyeremail"), (User) session.getAttribute("user"));
			session.removeAttribute("purchaseattemptgame");
			attribute.addFlashAttribute("success",
					"Congratulation! Your purchase was successful. Please check your email for the redeem code.");
			return "redirect:/userDashboard";
		}
		attribute.addFlashAttribute("error", "Incorrect otp try again.");
		return "redirect:/otp";
	}

	void sendCode(Long gameId, String email, User user) {
		Game game = gameService.getById(gameId);
		RedeemCodes redeemcode = redeemCodeService.getOneCode(gameId);
		redeemcode.setUser(user);
		redeemcode.setStatus("bought");
		redeemCodeService.save(redeemcode);
		mailUtil.sendEmail(email, "Your redeem code for game " + game.getName() + " is '" + redeemcode.getCode()
				+ "'. You should redeem this code from '" + game.getDownloadLink() + "' ", "Your redeem code");

	}

	@GetMapping("/search")
	public String search(Model model, @RequestParam String gamename) {
		List<Game> games = new ArrayList<>();
		Game game = gameService.getGameByName(gamename);
		if (game != null) {
			if (game.getRedeemCodes().stream().anyMatch(code -> "new".equals(code.getStatus()))) {
				games.add(game);
			}
		}
		model.addAttribute("partitionedGames", games);
		return "exploreGames";
	}

	@GetMapping("/history")
	public String history(HttpSession session, RedirectAttributes attribute, Model model) {
		if (session.getAttribute("user") == null) {
			attribute.addFlashAttribute("error", "Please login before buying");
			return "redirect:/login";
		}
		List<RedeemCodes> redeemCodes = redeemCodeService.getRedeemCodesByUserId((User) session.getAttribute("user"));
		List<Game> games = getDistinctGamesByUser(redeemCodes);
		Map<Game, List<String>> gameRedeemCodesMap = games.stream().collect(
				Collectors.toMap(game -> game, game -> redeemCodes.stream().filter(rc -> rc.getGame().equals(game))
						.map(RedeemCodes::getCode).collect(Collectors.toList())));
		model.addAttribute("gameRedeemCodesMap", gameRedeemCodesMap);
		return "history";
	}

	public List<Game> getDistinctGamesByUser(List<RedeemCodes> redeemCodes) {
		return redeemCodes.stream().map(RedeemCodes::getGame).distinct().collect(Collectors.toList());
	}

}
