package com.idontchop.datematchservice.controllers;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.idontchop.datematchservice.dtos.ReduceRequest;
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
	 * deprecated - before JWT
	 * 
	 * @param fromId
	 * @param to
	 * @return Match
	 *
	@RequestMapping ( 	value = "/${spring.application.type}/{from}/{to}",
						method = { RequestMethod.POST, RequestMethod.PUT} )
	public Match addMatch (	@PathVariable (name = "from", required = true) String from,
							@PathVariable (name = "to", required = true ) List<String> to) {
		return matchService.addMatch(from, to);
	}*/

	@RequestMapping ( 	value = "/${spring.application.type}/{to}",
			method = { RequestMethod.POST, RequestMethod.PUT} )
	public Match addMatch (	@PathVariable (name = "to", required = true ) List<String> to,
							Principal principal) {
		return matchService.addMatch(principal.getName(), to);
	}

	/**
	 * Delete Match. This deletes a match made the user.
	 */
	@DeleteMapping ( value = "/${spring.application.type}/{to}")
	public Match deleteMatch ( @PathVariable (name = "to", required = true) String to, Principal principal) {
		return matchService.deleteMatch(principal.getName(), to);
	}
	
	/**
	 * Delete User
	 * @param from
	 * @return Always "OK"
	 */
	@DeleteMapping ( value = "${spring.application.type}/deleteUser/{from}" )
	public RestMessage deleteUser ( @PathVariable (name = "from", required = true) List<String> from ) {
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
	
	@GetMapping ( value = "${spring.application.type}/MyMatches" )
	public Match getMyMatches (Principal principal) {
		return matchService.getUser(principal.getName());
	}
	
	/**
	 * 	/like/MyMatchesReduce
	 * 
	 * Allows frontend to supply a list of potentials to reduce the Match returned.
	 * 
	 * This is useful for the feed so frontend only deals with users who are in the feed.
	 * 
	 * @param reduceRequest
	 * @param principal
	 * @return
	 */
	@PostMapping ( value = "${spring.application.type}/MyMatchesReduce")
	public Match getMyMatchesWithReduce (@RequestBody ReduceRequest reduceRequest, Principal principal) {
		return matchService.getUserWithReduce(principal.getName(), reduceRequest.getPotentials());
	}
	
	/**
	 * Returns a list of users List<String> that satisfy the supplied type.
	 * Type can be one of three: to, from, connection
	 * 
	 * This is called by search-service / SearchPotentialsApi baseSearch
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	@GetMapping ( value = "${spring.application.type}/{name}/{type}")
	public List<String> getPotentials ( 
			@PathVariable ( name = "name", required = true) String name,
			@PathVariable ( name = "type", required = true) String type) {
		
		try {
		
			// switch service calls based on type parameter
			if ( type.equals("to")) {
				return matchService.getUserMatch(name);
			} else if (type.equals("from")) {
				return matchService.getUserIsMatch(name);
			} else if ( type.equals("connection")) {
				return matchService.getUserConnections(name);
			} else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type must be one of: to, from, connection");

		} catch ( NoSuchElementException ex ) {
			return List.of();		// user doesn't have entry
		}

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
