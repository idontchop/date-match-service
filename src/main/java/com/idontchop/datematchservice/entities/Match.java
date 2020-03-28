package com.idontchop.datematchservice.entities;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
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
	String name;
	
	// Users this user has matched to
	List<String> to = new ArrayList<>();
	
	// Users who matched to this user
	List<String> from	= new ArrayList<>();
	
	// Used for returning aggregations
	@BsonIgnore
	List<String> reduce = new ArrayList<>();
	
	public Match () {}
	public Match(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setUsername(String username) {
		this.name = username;
	}
	public List<String> getTo() {
		return to;
	}
	public void setTo(List<String> to) {
		this.to = to;
	}
	
	public void addFrom (String name) {
		from.add(name);
	}
	public void addTo (String name) {
		to.add(name);
	}
	public List<String> getFrom() {
		return from;
	}
	public void setFrom(List<String> from) {
		this.from = from;
	}
	public List<String> getReduce() {
		return reduce;
	}
	public void setReduce(List<String> reduce) {
		this.reduce = reduce;
	}

	
	
	

}
