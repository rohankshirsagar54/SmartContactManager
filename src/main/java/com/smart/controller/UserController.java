package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	
	// this technique is used when you need common data (like logged in user to display in navbar, in Add contact), 
	//so instead of creating handler , you create a method which will be called always when /user is invoked
	
	
	// this method for getting and  adding logged in user
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName=principal.getName();
		//System.out.println("Username:"+ userName);
		
		//get the user using userName;
		User user=userRepository.getUserByUsername(userName);
		
		model.addAttribute("user", user);
			
	}
	
	
	// to get logged in user in navbar- user dashboard
	
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	
		
	//open add contact handler

	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		
		model.addAttribute("title", "Add contact");
		model.addAttribute("contact", new Contact());
		
		return "normal/add_contact";
	}
	
	
	// processing add conatct form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute("contact") Contact contact, Principal principal) {      
		
		// just by giving Contact contact it will help in mapping the data from user form to properties given in Contact entity  
		//because the name filed given in thr form for each contact property matches with the Contact Entity class propery field
		
		
		
		
		// now conatct belongs to User and User has list of contacts
		// so first we will get the logged in user and in that user list we will add this contact & saved the user
		
		String name=principal.getName();
		User user=this.userRepository.getUserByUsername(name);
		
		//set user to the contact to get userId in contact
		contact.setUser(user);
		
		
		// Each User has list of contacts so now we will save that contact in user lists
		user.getContacts().add(contact);
		
		this.userRepository.save(user);
		
		System.out.println("Contact:"+ contact);
		
		System.out.println("added to the database");
		
		return "normal/add_contact";
		
	}
	
	

}
