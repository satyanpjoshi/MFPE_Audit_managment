package com.mfpe.model;

import java.util.ArrayList;
import java.util.Collection;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Component

@NoArgsConstructor

public class ProjectManagerDetails implements UserDetails{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String username;
	private String password;
	private String projectName;
	
	
	
	public ProjectManagerDetails(ProjectManager projectManager) {
		this.id = projectManager.getId();
		this.name = projectManager.getName();
		this.username = projectManager.getUsername();
		this.password = new BCryptPasswordEncoder(10).encode(projectManager.getPassword());
		this.projectName = projectManager.getProjectName();
	}
	
	public String getName() {
		return this.name;
	}

	public String getProjectName() {
		return this.projectName;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return new ArrayList<>();
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
