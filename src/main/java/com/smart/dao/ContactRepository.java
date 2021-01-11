package com.smart.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact,Integer> {
    
	@Query("from Contact as c where c.user.id=:userId")
	public List<Contact> findContactsByUser(@Param("userId")int userId);// this userId we are fetching from contact table, so same userId conatcts will be fetched
	
	
	/*
	 this userId in paramter of method we are getting from logged in user, then
	 we are firinf query to fetch all the list of contacts matching that userId
	  */
	
}
