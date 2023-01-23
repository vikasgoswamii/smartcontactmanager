package com.smartcontact.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.smartcontact.entites.Contacts;
import com.smartcontact.entites.User;

public interface ContactRepository extends JpaRepository<Contacts, Integer>{

	@Query(value = "select * from Contact where user_id = ?1", nativeQuery=true)
	//Current Page
	//Contact per page
	public Page<Contacts> findContactByUser(int userId, Pageable pageable);
	
	//For Search
	public List<Contacts> findByNameContainingAndUser(String name, User user);
	
	
	
}
