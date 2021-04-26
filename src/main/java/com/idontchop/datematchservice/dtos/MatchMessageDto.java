package com.idontchop.datematchservice.dtos;

public class MatchMessageDto {
	
	public MatchMessageDto(String from, String to) {
		this.fromId = from;
		this.toId = to;
	}
	
	String fromId;
	String toId;
	public String getFromId() {
		return fromId;
	}
	public void setFromId(String from) {
		this.fromId = from;
	}
	public String getToId() {
		return toId;
	}
	public void setToId(String to) {
		this.toId = to;
	}
	
	

}
