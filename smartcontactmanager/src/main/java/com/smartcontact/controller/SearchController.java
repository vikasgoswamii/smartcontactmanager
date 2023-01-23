package com.smartcontact.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smartcontact.dao.ContactRepository;
import com.smartcontact.dao.UserRepository;
import com.smartcontact.entites.Contacts;
import com.smartcontact.entites.User;

@RestController
public class SearchController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	
	//Handler For Searching Contact
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String Query, Principal p){
		
		
		User user = this.userRepository.getUserByUserName(p.getName());
		
		List<Contacts> searchedContact = this.contactRepository.findByNameContainingAndUser(Query, user);
	
		return ResponseEntity.ok(searchedContact);
	}
	
	
	
}
