package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;

@Controller
public class ForgotController {
	
	Random random=new Random(1000);
	
	@Autowired
	private BCryptPasswordEncoder br;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;

	@RequestMapping("/forgot")
	public String openEmailForm() {
		
		return "forgot_email_open";
	}
	
	
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {
		
		System.out.println(email);
		
		
		//generating random number
		
		int otp=random.nextInt(9999999);
		
		
		//write code to send otp to email
		
		String subject="OTP from SCM";
		String message=""
				+"<div style='border: 1px solid #e2e2e2; padding:20px'>"
				+"<h1>"
				+"OTP is: "
				+"<b>"+otp
				+"</b>"
				+"</h1>"
				+"</div>";
		
		String to=email;
		
		boolean flag=this.emailService.sendMail(subject, message, to);
		
		if(flag) {
			
			//keeping otp and email both in session to verify
			session.setAttribute("myOtp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
			
		}
		else {
			
			session.setAttribute("message","Check your email id");
		
			return "forgot_email_open";
		}		
	}
	
	
	//verify otp
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp") int otp, HttpSession session) {
		
		int myotp=(int)session.getAttribute("myOtp");
		String email=(String)session.getAttribute("email");
		
		if(myotp==otp) {
			
			
			
			
			//1. fetch user from email
			User user=this.userRepository.getUserByUsername(email);
			
			if(user==null) {
				
				session.setAttribute("message","User does not exist with this email!!");
				
				return "forgot_email_open";
			}
			else {
				
				////show password page form
				
			}
			
			
			return "password_change_form";
		}
		else {
			
			session.setAttribute("message", "You have entered Wrong OTP!!");
			return "verify_otp";
		}
		
		
	}
	
	
	
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword, HttpSession session) {
		
		
		String email=(String)session.getAttribute("email");
		User user=this.userRepository.getUserByUsername(email);
		
		user.setPassword(this.br.encode(newpassword));
		
		this.userRepository.save(user);
		
		return "redirect:/signin?change=password chnaged successfully!!";
		
	}
}
