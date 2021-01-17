package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact,Integer> {
    
	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactsByUser(@Param("userId")int userId, Pageable pageable);// this userId we are fetching from contact table, so same userId, conatcts will be fetched
	
	
	/*
	 this userId in paramter of method we are getting from logged in user, then
	 we are firinf query to fetch all the list of contacts matching that userId
	  */
	
	
	/*
	 * 
	 * this pagebale paramter will have 2 information 
	 * 1) current information    and
	 * 2) contact per page you want to display
	 */
	
	
	
	// search contacts
	public List<Contact> findByNameContainingAndUser(String name, User user);
	
	
	/*
	 this findByName is a custom method and will act as where caluse and containing will give all the names that are near to 
	the input, AndUser will make sue that only contact belonging to that particular user will be searched
	*/
	
	
	
	
}
