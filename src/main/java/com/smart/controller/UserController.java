package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

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
			contact.setImage("contact.png");
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
	//per page=5
	//current page=0[page]
	@GetMapping("/show_contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model m,Principal principal) {
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
		
		//for pagination
		/*PageRequest is a child class of Pageable interface, so u can provide the child value to parent object
		and this we are sendig to UserRepository*/
		Pageable pageable=PageRequest.of(page, 3);
		
		Page<Contact> contacts=this.contactRepository.findContactsByUser(user.getId(), pageable);
		
		
		m.addAttribute("contacts", contacts);  // this is contacts of user
		m.addAttribute("currentPage",page);    //current page
		m.addAttribute("totalPages", contacts.getTotalPages());    //total page
		
		return "normal/show_contacts";
	}
	
	
	
	//showing particular contact details
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		
		
		Optional<Contact> contactOptional=this.contactRepository.findById(cId);
		Contact contact= contactOptional.get();
		
		
		//security check to ensure that the logged in user can see only his/her contact
		String name=principal.getName();
		User user=this.userRepository.getUserByUsername(name);
		
		           //this checks logged in user id is equal to contact (which belongs to user) his id is same  
		if(user.getId()==contact.getUser().getId()) {
			model.addAttribute("model", contact);
			model.addAttribute("title", contact.getName());
		}
		
		
		
		return "normal/contact_detail";
	}

	
	
	//delete the contact
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model model, Principal principal, HttpSession session) {
		
		try {
		
		Optional<Contact> contactOptional=this.contactRepository.findById(cId);
		
		Contact contact=contactOptional.get();
		
		
		//check for security same like up for
		String name=principal.getName();
		User user=this.userRepository.getUserByUsername(name);
		
		if(user.getId()==contact.getUser().getId()) {
			
			contact.setUser(null);   //unlinking the the contact to user first due to Cascade condition in Entity class
			
			
			       //delete file from folder as well
			
                    
			
		this.contactRepository.delete(contact);
		}
		
		session.setAttribute("message", new Message("contact deleted successfully","success"));
		}
		
		catch(Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/show_contacts/0";
		
		
	}

	
	//open update handler form
	@PostMapping("/update-contact/{cId}")
	public String updateContacts(@PathVariable("cId") Integer cId, Model model) {
		
		
		model.addAttribute("title", "Update contact");
		
		Optional<Contact> contactOptional=this.contactRepository.findById(cId);
		
		Contact contact=contactOptional.get();
		
		model.addAttribute("contact",contact);
		
		return "normal/update_form";
		
	}
	
	
	//update  contact handler
	@RequestMapping(value="/process-update", method=RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, Model model, 
			HttpSession session, Principal principal) {
		
		
		try {
			
			//first get old contact details to get the old image
			Contact oldcontact=this.contactRepository.findById(contact.getcId()).get();
			
			//if image is updated then need to rewrite
			if(!file.isEmpty()) {
				
				//rewrite the file and delete the old file
				
				// 1. delete old photo
				    
				File files1=new ClassPathResource("static/img").getFile();
				File newFile=new File(files1, oldcontact.getImage());
				
				newFile.delete();
				
				
				// 2.update new photo
				
				File files=new ClassPathResource("static/img").getFile();
				
				Path path=Paths.get(files.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
				
			}
			else {
				
				contact.setImage(oldcontact.getImage());
			}
			
			//you need to set/update the user id in the contact table
			//because when you update contact uid column in contact table is set to null
			
			
			
			User user=this.userRepository.getUserByUsername(principal.getName());
			
			contact.setUser(user);
			this.contactRepository.save(contact);
			
			session.setAttribute("message", new Message("Your contact is updated!!","success"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println(contact.getName());
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	
	
	//your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		
		model.addAttribute("title", "Profile Page");
		
		return "normal/profile";
	}
	
	
	
	
	
	//open setting handler
	@GetMapping("/settings")
	public String openSetting() {
		
		return "normal/settings";
	}
}
