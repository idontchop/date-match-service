package com.idontchop.datematchservice.controllers;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.idontchop.datematchservice.dtos.MatchDto;
import com.idontchop.datematchservice.dtos.ReduceRequest;
import com.idontchop.datematchservice.dtos.RestMessage;
import com.idontchop.datematchservice.entities.Match;
import com.idontchop.datematchservice.services.MatchService;
import com.idontchop.datematchservice.services.ReduceService;

/**
 * Manipulation for users and matches here since they mainly go together.
 * A match always contains two users and will auto create the user
 * on either side. Though if a target user doesn't exist, this will
 * be a bug in front end code and logged. (TODO)
 * 
 * For API endpoints. "match" = spring.application.type
 * This allows us to be clear which service is being called. For blocks,
 * the endpoint would be block where match.
 * 
 *  /match/{from}/{to}
 * 
 *
 * 
 * @author nate
 *
 */
@RestController
public class MainController {
	
	@Value ("${spring.application.type}")
	private String matchType;
	
	@Value ("${server.port}")
	private String serverPort;
	
	@Value("${spring.application.name}")
	private String appName;
	
	@Autowired
	MatchService matchService;
	
	@Autowired
	ReduceService reduceService;
	
	
	/**
	 * Returns the relative complement (difference) of the potentials list in ReduceRequest 
	 * with respect to the supplied username in this database.
	 * 
	 * potentials / database
	 * 
	 * This will be used in services such as Blocks.
	 * 
	 * "Find users in potentials that are not in the database on this service."
	 * "Find users that are not blocked by this user or this user is blocked by."
	 * 
	 * @param reduceRequest
	 * @return
	 */
	@GetMapping(value = "/${spring.application.type}/difference")
	public List<String> reduce(@RequestBody @Valid ReduceRequest reduceRequest) {
		return reduceService
				.findDifference(reduceRequest.getName(), reduceRequest.getPotentials())
				.getReduce();
	}
	
	/**
	 * Returns the Intersection (matching) of the potentials list in ReduceRequest
	 * with respect to the supplied username in the database.
	 * 
	 * potentials ∩ database
	 * 
	 * This is useful for matches such as Likes.
	 * 
	 * "Find users in potentials that are in the database on this service in to field."
	 * "Find users liked by this user."
	 * 
	 * @param reduceRequest
	 * @return
	 */
	@GetMapping (value = "/${spring.application.type}/intersection/to")
	public List<String> liked(@RequestBody @Valid ReduceRequest reduceRequest) {
		return reduceService
				.findIntersection(reduceRequest.getName(), reduceRequest.getPotentials(), true)
				.getReduce();
	}
	
	/**
	 * Returns Intersection in from. (See liked)
	 * 
	 * "Find users that like this user."
	 * 
	 * @param reduceRequest
	 * @return
	 */
	@GetMapping (value = "/${spring.application.type}/intersection/from")
	public List<String> isLiked(@RequestBody @Valid ReduceRequest reduceRequest) {
		return reduceService
				.findIntersection(reduceRequest.getName(), reduceRequest.getPotentials(), false)
				.getReduce();
	}
	
	/**
	 * Returns connections (mutual matches) of the potentials list in Reduce Request
	 * with respect to the supplied username in the database.
	 * 
	 * This is useful to find Connections.
	 * 
	 * "Find users in potentials in the database that also have the supplied
	 * user in their TO list."
	 * "Find users liked by this user who liked back."
	 * 
	 * @param reduceRequest
	 * @return
	 */
	@GetMapping (value = "/${spring.application.type}/connection")
	public List<String> connection(@RequestBody @Valid ReduceRequest reduceRequest) {
		return reduceService
				.findFullIntersection(reduceRequest.getName(), reduceRequest.getPotentials())
				.getReduce();
	}
	
	/**
	 * All IndexOutofBounds Exception mean the username was not found.
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler (IndexOutOfBoundsException.class)
	public ResponseEntity<String> handleOutOfBounds ( IndexOutOfBoundsException ex ) {
		return ResponseEntity.notFound().build();
	}
	
	@GetMapping("/helloWorld")
	public RestMessage helloWorld () {
		String serverAddress,serverHost;
		try {
			serverAddress = NetworkInterface.getNetworkInterfaces().nextElement()
					.getInetAddresses().nextElement().getHostAddress();
		} catch (SocketException e) {
			serverAddress = e.getMessage();
		}
		try {
			serverHost = NetworkInterface.getNetworkInterfaces().nextElement()
					.getInetAddresses().nextElement().getHostName();
		} catch (SocketException e) {
			serverHost = e.getMessage();
		}
		return RestMessage.build("Hello from " + matchType)
				.add("service", appName)
				.add("host", serverHost)
				.add("address", serverAddress)
				.add("port", serverPort);
			
	}

}
