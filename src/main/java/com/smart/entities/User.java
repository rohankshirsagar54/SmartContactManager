package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name="User")
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Column(name="name")
	@NotBlank(message="Name field is required!!")
	@Size(min=2, max=20, message="min 2 and max 20 characters are allowed !!")
	private String name;
	
	@Column(name="email",unique = true)
	@NotBlank
    @Email(message = "Please enter a valid e-mail address")
	private String email;
	
	
	@Column(name="password")
	@NotBlank(message="Password cannot be blank")
	private String password;
	
	@Column(name="role")
	private String role;
	
	@Column(name="enabled")
	private boolean enabled;
	
	@Column(name="imageUrl")
	private String imageUrl;
	
	
	@NotBlank(message="About cannot be blank")
	@Size(min=2, max=5000, message="min 2 characters are allowed !!")
	@Column(name="about")
	private String about;
	
	@OneToMany(cascade= CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="user", orphanRemoval=true)
	private List<Contact> contacts=new ArrayList<>();

	//orphanRemoval=true means whenever child entity(contact) is unlinked with parent entity(user), 
	//then while delete it can be removed direclty

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(int id, String name, String email, String password, String role, boolean enabled, String imageUrl,
			String about) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
		this.enabled = enabled;
		this.imageUrl = imageUrl;
		this.about = about;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}
	
	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
				+ ", enabled=" + enabled + ", imageUrl=" + imageUrl + ", about=" + about + ", contacts=" + contacts
				+ "]";
	}

	
	
	
	
	
	
}
