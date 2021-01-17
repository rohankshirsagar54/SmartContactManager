package com.smart.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendMail(String subject, String message, String to) {

		boolean f = false;

		String host = "smtp.gmail.com";
		String from = "rohankshirsagar54@gmail.com";

		// get the system properties
		Properties properties = System.getProperties();

		// setting important information in properties object

		// host set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// step 1 to get the session object
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication("rohankshirsagar808@gmail.com", "Ramesh@1994");
			}

		});

		session.setDebug(true);

		// step 2: compose the message(text, multimedia)

		MimeMessage m = new MimeMessage(session);

		try {

			// from email id
			m.setFrom(from);

			// adding recepient to
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// adding subject to meesage
			m.setSubject(subject);

			// adding text to meesage
			//m.setText(message);
			
			m.setContent(message, "text/html");

			// Step 3: send the meesage
			Transport.send(m);

			System.out.println("Send Successfully");

			f = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return f;
	}

}
