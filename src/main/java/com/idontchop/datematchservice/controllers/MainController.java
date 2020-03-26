package com.idontchop.datematchservice.controllers;

import java.net.NetworkInterface;
import java.net.SocketException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.idontchop.datematchservice.dtos.ReduceRequest;
import com.idontchop.datematchservice.dtos.RestMessage;

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
	
	
	/**
	 * Returns the relative complement (difference) of the potentials list in ReduceRequest 
	 * with respect to the supplied username in this database.
	 * 
	 * potentials / database
	 * 
	 * This will be used in services such as Blocks.
	 * 
	 * "Find users in potentials that are not in the database on this service."
	 * "Find users that are not blocked by this user."
	 * 
	 * @param reduceRequest
	 * @return
	 */
	@GetMapping(value = "/${spring.application.type}/difference")
	public RestMessage reduce(@RequestBody @Valid ReduceRequest reduceRequest) {
		return RestMessage.build("good");
	}
	
	/**
	 * Returns the Intersection (matching) of the potentials list in ReduceRequest
	 * with respect to the supplied username in the database.
	 * 
	 * potentials ∩ database
	 * 
	 * This is useful for matches such as Likes.
	 * 
	 * "Find users in potentials that are in the database on this service."
	 * "Find users liked by this user."
	 * 
	 * @param reduceRequest
	 * @return
	 */
	@GetMapping (value = "/${spring.application.type}/intersection")
	public RestMessage match(@RequestBody @Valid ReduceRequest reduceRequest) {
		return RestMessage.build("good");
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
