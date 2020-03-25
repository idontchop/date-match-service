package com.idontchop.datematchservice.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.idontchop.datematchservice.entities.Match;
import com.idontchop.datematchservice.repositories.MatchRespository;

@Service
public class MatchService {
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	MatchRespository matchRepository;
	
	/**
	 * Simple add from username to...
	 * 
	 * Two records will need to be updated.
	 * 
	 * @param username
	 * @param to
	 * @return
	 */
	public Match addMatch (String username, String to ) {
		Match user = retrieveOrAdd(username);
		Match toUser = retrieveOrAdd(to);
		
		/*
		 * If we end up doing a repo.save, then the save in retrieveoradd not necessary
		 * but if we use mongotemplate.push, then it will need to be saved before (later
		 * probably better)
		 */
		
	}
	
	/**
	 * Retrieves the document from the db, or adds a new one.
	 * 
	 * @param username
	 * @return
	 */
	private Match retrieveOrAdd (String username ) {
		return 
			matchRepository.findByName(username)
				.orElse(new Match(username));
		
		/* Not necessary right?
		return match.orElseGet( () -> {
			
			return matchRepository.save( new Match (username) );
		}); */
	}

}
