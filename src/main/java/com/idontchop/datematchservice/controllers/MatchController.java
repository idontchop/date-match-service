package com.idontchop.datematchservice.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.idontchop.datematchservice.entities.Match;
import com.idontchop.datematchservice.services.MatchService;

/**
 * Holds manipulation endpoints for adding / removing / querying
 * database info on matches.
 * 
 * /match/from/to
 * 
 * POST/PUT - used to add matches, will add user automatically
 * GET 		- For situations where all of a user's matches are needed
 * DELETE	- Deletes supplied matches
 * 
 * @author micro
 *
 */
@RestController
public class MatchController {
	
	@Autowired
	MatchService matchService;
	
	@Value ("${spring.application.type}")
	private String matchType;
	
	@Value ("${server.port}")
	private String serverPort;
	
	@Value("${spring.application.name}")
	private String appName;
	
	@RequestMapping ( 	value = "/${spring.application.type}/{from}/{to}",
						method = { RequestMethod.POST, RequestMethod.PUT} )
	public Match addMatch (	@PathVariable (name = "from", required = true) String from,
							@PathVariable (name = "to", required = true ) List<String> to) {
		return matchService.addMatch(from, to);
	}
	

}
