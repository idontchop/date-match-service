package com.idontchop.datematchservice.dtos;

import java.util.ArrayList;
import java.util.List;

import com.idontchop.datematchservice.entities.Match;

public class MatchDto extends Match {
	
	public MatchDto () { super(); }
	List<String> reduce = new ArrayList<>();
	
	public List<String> getReduce() {
		return reduce;
	}
	public void setReduce(List<String> reduce) {
		this.reduce = reduce;
	}
	
	

}
