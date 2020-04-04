package com.idontchop.datematchservice.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.idontchop.datematchservice.dtos.RestMessage;
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
	
	/**
	 * Add Match
	 * 
	 * @param from
	 * @param to
	 * @return Match
	 */
	@RequestMapping ( 	value = "/${spring.application.type}/{from}/{to}",
						method = { RequestMethod.POST, RequestMethod.PUT} )
	public Match addMatch (	@PathVariable (name = "from", required = true) String from,
							@PathVariable (name = "to", required = true ) List<String> to) {
		return matchService.addMatch(from, to);
	}
	
	/**
	 * Delete Match
	 * @param from
	 * @return Always "OK"
	 */
	@DeleteMapping ( value = "${spring.application.type}/{from}" )
	public RestMessage deleteMatch ( @PathVariable (name = "from", required = true) List<String> from ) {
		matchService.deleteUser(from);
		return RestMessage.build("OK");
	}
	
	/**
	 * Returns requested Users complete with matches.
	 * 
	 * @param from
	 * @return List
	 */
	@GetMapping ( value = "${spring.application.type}/{from}" )
	public List<Match> getMatch ( @PathVariable (name = "from", required = true) List<String> from ) {
		return matchService.getUser(from);
	}
	
	/**
	 * No return value.
	 * 200 if found
	 * 404 if not found
	 * 
	 * switch can be to / from / both
	 */
	@GetMapping ( value = "${spring.application.type}/{choice}/{from}/{to}")
	public ResponseEntity<String> getSpecificMatch ( @PathVariable ( name = "from", required = true) String from,
			@PathVariable ( name = "choice", required = true) String choice,
			@PathVariable ( name = "to", required = true ) String to ) {
		
		// build arguments 
		boolean checkTo = false, checkFrom = false;
		if ( choice.equalsIgnoreCase("to")) {
			checkTo = true;
		} else if ( choice.equalsIgnoreCase("from")) {
			checkFrom = true;
		} else if ( choice.equalsIgnoreCase("both")) {
			checkTo = true; checkFrom = true;
		} else {
			return ResponseEntity.badRequest().body("choice allowed arguments: to/from/both");
		}
		
		return matchService.userHasMatch(from, to, checkTo, checkFrom) ? 
				ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}
	

}
