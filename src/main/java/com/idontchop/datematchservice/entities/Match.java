package com.idontchop.datematchservice.entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Generic match entity.
 * 
 * Blocks, Hides, Likes, Favorites, etc generally work the same way.
 * The name of which can be determined by the properties.
 * 
 * @author nate
 *
 */
@Document
public class Match {
	
	@Id
	private String id;
	
	// username supplied by API
	String username;
	
	// Users this user has matched to
	List<String> to = new ArrayList<>();
	
	// Users who matched to this user
	List<String> from	= new ArrayList<>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public List<String> getTo() {
		return to;
	}
	public void setTo(List<String> to) {
		this.to = to;
	}
	public List<String> getFrom() {
		return from;
	}
	public void setFrom(List<String> from) {
		this.from = from;
	}

	
	
	

}
