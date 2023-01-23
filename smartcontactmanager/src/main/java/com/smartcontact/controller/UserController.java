package com.smartcontact.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartcontact.dao.ContactRepository;
import com.smartcontact.dao.UserRepository;
import com.smartcontact.entites.Contacts;
import com.smartcontact.entites.User;
import com.smartcontact.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	Logger logger = org.slf4j.LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	// Method for adding common data
	@ModelAttribute
	public void addCommonData(Model m, Principal p) {

		String email = p.getName();

		logger.info(email);

		User user = userRepository.getUserByUserName(email);

		m.addAttribute("user", user);

	}

	@RequestMapping(method = RequestMethod.GET, value = "/user_dashboard")
	public String dashBoard(Model m, Principal p) {

		m.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	// Open add form handler
	@RequestMapping(method = RequestMethod.GET, value = "/add-contact")
	public String openAddHandler(Model m) {

		m.addAttribute("title", "AddContact");
		m.addAttribute("Contact", new Contacts());

		return "normal/add_contact_form";
	}

	// Adding Contact
	@PostMapping("/process-contact")
	public String addContact(@ModelAttribute Contacts contact, @RequestParam("profileImage") MultipartFile file,
			HttpSession session, Principal p) throws IOException {

		String userName = p.getName();

		User user = userRepository.getUserByUserName(userName);

		contact.setUser(user);

		user.getContacts().add(contact);

		try {

			if (file.isEmpty()) {
				logger.info("File is Empty");
				contact.setImage("default.png");
			} else {

				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsoluteFile() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				logger.info("File is Uploaded!!!");

			}

			this.userRepository.save(user);

//			logger.info(user.toString());

			// Success Message to be Print
			session.setAttribute("message", new Message("Contact Successfully added !! Add new one...!!", "success"));

		} catch (Exception e) {
			e.printStackTrace();
			// Error message to be print
			session.setAttribute("message", new Message("Something went wrong!! Please try again...", "danger"));
		}

		return "normal/add_contact_form";
	}

	//Show all contacts
	@GetMapping("/show-contact/{page}")
	public String ViewContactHandler(@PathVariable("page") int page, Model m, Principal principal) {

		m.addAttribute("title", "Show View Contacts");

		String userName = principal.getName();

		User user = this.userRepository.getUserByUserName(userName);

		// Pagination
		// We have to pass two things:-
		// 1. Current Page Number
		// 2. Contact Per page
		Pageable pageable = PageRequest.of(page, 3);

		Page<Contacts> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);

		m.addAttribute("contacts", contacts);
		m.addAttribute("currentpage", page);
		m.addAttribute("totalpage", contacts.getTotalPages());

		return "normal/show_contact";
	}

	//Get Contact Profile
	@GetMapping(value = "/contact/{cId}")
	public String contactDetails(@PathVariable("cId") int cId, Model m, Principal p) {

		Optional<Contacts> contactOptional = this.contactRepository.findById(cId);
		Contacts contact = contactOptional.get();

		String username = p.getName();

		User userDetails = this.userRepository.getUserByUserName(username);

		if (userDetails.getId() == contact.getUser().getId()) {
			m.addAttribute("title", "Show Profile");
			m.addAttribute("contact", contact);
		}
		return "normal/contact_detail";
	}

	//Delete Contact 
	@GetMapping("/delete-contact/{cId}")
	public String deleteContact(@PathVariable("cId") int cId, Model m, HttpSession session) {

		Contacts contact = this.contactRepository.findById(cId).get();

		contact.setUser(null);

		//1. We have to delete profile picture from location also here
		//2. Only Authentic User can delete your own contact no'one else
		
		this.contactRepository.delete(contact);

		session.setAttribute("message", new Message("Contact Deleted Successfully...", "success"));

		return "redirect:/user/show-contact/0";
	}

	//Open Update form 
	@PostMapping("/update-contact/{cId}")
	public String upadteHandler(@PathVariable("cId") int cId,  Model m) {
		
		Contacts contact = this.contactRepository.findById(cId).get();
		
		m.addAttribute("contact", contact);
		m.addAttribute("title", "Upate Contact");
		return "normal/update_form";
	}
	
	//Update Contact Handler
	@PostMapping(value="/process-update-contact")
	public String processUpdate(@ModelAttribute Contacts contact, @RequestParam("profileImage") MultipartFile image, HttpSession session, Principal p) {
		
		logger.info(contact.toString());
		
		try {
			
			Contacts oldContactDetail = this.contactRepository.findById(contact.getcId()).get();
			
			
			if(!image.isEmpty()) {
				
				//Delete Old Photo
				if(!oldContactDetail.getImage().equals("default.png")) {
					File deleteFile = new ClassPathResource("static/img").getFile();
					File file1 = new File(deleteFile, oldContactDetail.getImage());
					file1.delete();
				}
		
				//Update Old Photo
				
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsoluteFile() + File.separator + image.getOriginalFilename());
				Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(image.getOriginalFilename());
			
			}
			else {
				//Image will be same if Image not change while updating
				contact.setImage(oldContactDetail.getImage());
			}
			
			
			User user = this.userRepository.getUserByUserName(p.getName());
			
			contact.setUser(user);
			
			this.contactRepository.save(contact);

			session.setAttribute("message", new Message("Your Contact Successfully Updated...", "success"));
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/user/contact/" + contact.getcId();
	}
	
	//User Profile Handler
	@GetMapping("/profile")
	public String profileHandler(Model m) {
		
		
		m.addAttribute("title", "Profile");
		return "/normal/user_profile";
	}
	
	
	//Open Setting Handler
	@GetMapping(value="/open-setting")
	public String openHandler(Model m) {
		
		m.addAttribute("title", "Setting");
		
		return "normal/setting";
	}
	
	
	
	
}
