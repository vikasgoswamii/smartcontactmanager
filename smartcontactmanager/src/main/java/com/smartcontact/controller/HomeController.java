package com.smartcontact.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.dao.UserRepository;
import com.smartcontact.entites.User;
import com.smartcontact.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	Logger logger = org.slf4j.LoggerFactory.getLogger(HomeController.class);

	// Handler for home page
	@RequestMapping("/")
	public String home(Model m) {

		m.addAttribute("tittle", "Home - smart contact manager");

		return "home";

	}

	// Handler for About page
	@RequestMapping("/about")
	public String about(Model m) {

		m.addAttribute("tittle", "About - smart contact manager");

		return "about";

	}

	// Handler for About page
	@RequestMapping("/signup")
	public String signUp(Model m) {

		m.addAttribute("tittle", "SignUp - Smart Contact Manager");
		m.addAttribute("user", new User());
		return "signup";

	}

	// Handling for Register
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result1,
			@RequestParam(value = "term&condition", defaultValue = "false") boolean agreement, Model m,
			HttpSession session) {

		try {

			if (!agreement) {
				logger.info("You have not agreed terms and conditions!!!");
				throw new Exception("You have not agreed terms and conditions!!!");

			}

			if (result1.hasErrors()) {

				logger.info("Errors: " + result1.toString());
				m.addAttribute("user", user);
				return "signup";
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			User result = userRepository.save(user);

			m.addAttribute("user", result);

			logger.info(user.toString());


			session.setAttribute("message", new Message("Successfully Registered !!!", "alert-success"));

			return "redirect:/signup/";
		} catch (Exception e) {
			e.printStackTrace();
			m.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong !!!  " + e.getMessage(), "alert-danger"));
			return "signup";
		}

	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/signin")
	public String customLogin(Model m) {

		m.addAttribute("title", "Login - Smart Contact Manager");
		return "login";
		
	}
	
	
	@RequestMapping(value = "/user/login", method = RequestMethod.POST)
	public String loginDashboard() {
		
		return "login_dashboard";
		
	}
	
	
	
	

}
