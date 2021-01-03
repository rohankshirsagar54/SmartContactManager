package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/")
	public String home(Model model) {
		
		
		model.addAttribute("title","Home-Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		
		model.addAttribute("title", "About-Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		
		model.addAttribute("title", "Register-Smart Contact Manager");
		
 //sending blank object of user, when coming from home page -->see the signup.html 
		//where it takes the reference of that blank object in form
		model.addAttribute("user", new User());    
		return "signup";
	}
	
	
	// this handler registering user
	@RequestMapping(value= "/do_register", method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult results, @RequestParam(value="agreement", defaultValue="false") boolean agreement, Model model, HttpSession session) {        
		
		try {
			
			if(!agreement) {
				
				throw new Exception("please agree terms and condition");
			}
			
			if(results.hasErrors()) {
				
				//System.out.println("ERROR" + results.toString());
				
				// this will work if validation fails or any field while registering is left empty will be sent back to user register form
				model.addAttribute("user", user);
				
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			//saving new user by taking dao object
			User result=this.userRepository.save(user);
			
			//this if registration is successful new empty blank user is sent
			model.addAttribute("user", new User());
			
			//newly registered user is kept in session
			session.setAttribute("message", new Message("Registration successful!!","alert-success" ));
			
			//after successful registration signup page is return 
			return "signup";
			
		}   
		catch (Exception e) {
			e.printStackTrace();
			
            // this will work if validation fails or any field while registering is left empty will be sent back to user register form
			model.addAttribute("user",user);
			
			//unsuccessful registered user is kept in session
			session.setAttribute("message", new Message("something went wrong!!"  +e.getMessage(), "alert-danger" ));
			return "signup";
		}
		
		
	}

	
	//handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		
		model.addAttribute("title", "login page");
		return "login";
	}
	
	
}
