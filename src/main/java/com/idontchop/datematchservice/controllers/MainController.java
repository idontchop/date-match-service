package com.idontchop.datematchservice.controllers;

import java.net.NetworkInterface;
import java.net.SocketException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.idontchop.datematchservice.dtos.RestMessage;

/**
 * Manipulation for users and matches here since they mainly go together.
 * A match always contains two users and will auto create the user.
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
