package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
    
	@Autowired
	private ContactRepository contactRepository;
	
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
	public String processContact(@ModelAttribute("contact") Contact contact, 
			@RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session) {      
		
		try {
			
		// just by giving Contact contact it will help in mapping the data from user form to properties given in Contact entity  
		//because the name filed given in thr form for each contact property matches with the Contact Entity class propery field
		
		//last 1: multipart to fetch the image using @RequestParam
		
		
		// now conatct belongs to User and User has list of contacts
		// so first we will get the logged in user and in that user list we will add this contact & saved the user
		
		String name=principal.getName();
		User user=this.userRepository.getUserByUsername(name);
		
		
		//last 2: processing image
		if(file.isEmpty()) {
			System.out.println("File is empty");
		}
		else {
			//upload the file to folder
			contact.setImage(file.getOriginalFilename());
			File files=new ClassPathResource("static/img").getFile();
			
			Path path=Paths.get(files.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			System.out.println("Image is uploaded");
		}
		
		
		
		//set user to the contact to get userId in contact
		contact.setUser(user);
		
		
		// Each User has list of contacts so now we will save that contact in user lists
		user.getContacts().add(contact);
		
		this.userRepository.save(user);
		
		System.out.println("Contact:"+ contact);
		
		//success message if contact added successfully
		session.setAttribute("message", new Message("Your Contact is added !!","success"));
		}
		
		catch (Exception e) {
			e.printStackTrace();
			
			//error message if contact not added successfully
			session.setAttribute("message", new Message("Somethign went wrong, Try Again","danger"));
		}
		
		return "normal/add_contact";
		
	}
	
	
	//show contacts handler
	@GetMapping("/show_contacts")
	public String showContacts(Model m,Principal principal) {
		m.addAttribute("title", "Show User Contacts");
		
		/*
		 * 1 way to get contact trhough userRepository
		 * 
		//get contact list from logged in user
		String name=principal.getName();
		
		User user=this.userRepository.getUserByUsername(name);
		
		List<Conatct>conatcts=user.getContacts();
		*/
		
		// 2nd way-- get first user id with Principal
         String name=principal.getName();
		
		User user=this.userRepository.getUserByUsername(name);
		
		List<Contact> contacts=this.contactRepository.findContactsByUser(user.getId());
		
		
		m.addAttribute("contacts", contacts);
		
		
		
		return "normal/show_contacts";
	}
	

}
