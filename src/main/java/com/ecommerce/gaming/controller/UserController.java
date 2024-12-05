package com.ecommerce.gaming.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.gaming.model.User;
import com.ecommerce.gaming.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

	@Autowired
	UserService userService;

	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@GetMapping({ "/", "/home" })
	public String homepage() {

		return "homepage";
	}

	@GetMapping("/login")
	public String login() {

		return "login";
	}

	@PostMapping("/login")
	public String postLogin(@RequestParam String email, @RequestParam String password,
			RedirectAttributes attribute, HttpSession session) {
		User user= userService.getByEmail(email);
		if (user== null) {
			attribute.addFlashAttribute("error", "User does not exists.");
            return "redirect:/login";
		}
		if(!userService.verifyPassword(password, user, encoder)) {
			attribute.addFlashAttribute("error", "Incorrect Password");
			return "redirect:/login";
		}
		if(user.getRole().equals("admin")) {
			session.setAttribute("admin", user);
			return "redirect:/adminDashboard";
		}
		if(session.getAttribute("purchaseattemptgame")!=null) {
			session.setAttribute("user", user);
			Long id = (Long) session.getAttribute("purchaseattemptgame");
			return "redirect:/buy?id=" + id;
		}
		session.setAttribute("user", user);
		return "redirect:/userDashboard";
	}

	@GetMapping("/signup")
	public String signup() {

		return "signup";
	}

	@PostMapping("/signup")
	public String postSignup(@ModelAttribute User user, RedirectAttributes attribute) {
		if (!user.getPassword().equals(user.getPassword2())) {
			attribute.addFlashAttribute("error", "Password does not match.");
			return "redirect:/signup";
		}

		if (userService.getByEmail(user.getEmail()) != null || userService.getByPhone(user.getPhone()) != null) {
			attribute.addFlashAttribute("error", "User already exists.");
			return "redirect:/signup";
		}
		user.setPassword(encoder.encode(user.getPassword()));
		user.setRole("user");
		userService.save(user);
		attribute.addFlashAttribute("success", "Registered successfully.");
		return "redirect:/login";
	}
	
	@GetMapping("/userDashboard")
	public String userDashboard(HttpSession session, RedirectAttributes attribute, Model model) {
		if(session.getAttribute("user")==null) {
			attribute.addFlashAttribute("error", "Please login");
			return "redirect:/login";
		}
		User user =(User) session.getAttribute("user");
		model.addAttribute("username", user.getFullName());
		return "userDashboard";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session, RedirectAttributes attribute) {
		attribute.addFlashAttribute("success", "Logout Successfull.");
		session.invalidate();
		return "redirect:/login";
	}
	
	
	
	
	
	
	
	
	
}
