package com.idontchop.datematchservice.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.idontchop.datematchservice.entities.Match;
import com.idontchop.datematchservice.repositories.MatchRespository;

@Service
public class MatchService {
	
	// Database fields in the Match Document
	// TODO: set these to property values
	private final String DBNAME 	= 	"name";
	private final String DBTO		=	"to";
	private final String DBFROM		=	"from";
	
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
	public Match addMatch (String name, List<String> to ) {
		Match user = retrieveOrAdd(name);
		List<Match> toUser = retrieveOrAdd(to);
		
		Query query = new Query();
		query.addCriteria(Criteria.where("name").is(name));
		
		Update update = new Update().addToSet("to").each(to);
		
		mongoTemplate.updateFirst(query, update, Match.class);
		
		
		return null;
	}
	
	/**
	 * Takes a list of the Tos and updates their records
	 * or creates a new record. Any record saved will need
	 * the from field updated.
	 * 
	 * @param to
	 */
	public void updateOrAddTos ( String name, List<String> to ) {
		
		// Creates a list of users not found in db
		// Easy but not optimal - Should collection useing MongoTemplate
		List<String> adds = matchRepository.findNameByNameIn(to)
				.stream().map(Match::getName).collect(Collectors.toList());
		
		// Find users not in DB
		Set<String> toDif = new HashSet<>(to);
		toDif.removeAll(adds);

		// Add the new user and save their from
		toDif.forEach( newUser -> {
			Match newMatch = new Match(newUser);
			newMatch.addFrom(name);
			matchRepository.save(newMatch);
		});
		
		// Update those users already saved by using native mongo
		Query query = new Query ();
		query.addCriteria(Criteria.where(DBNAME).in(adds));
		Update update = new Update().addToSet(DBTO, name);
		mongoTemplate.updateFirst(query, update, Match.class);
		
	}
	
	/**
	 * Retrieves the document from the db, or adds a new one.
	 * 
	 * @param username
	 * @return
	 */
	private Match retrieveOrAdd (String username ) {
		 
		Optional<Match> match = matchRepository.findByName(username);
		
		return match.orElseGet( () -> {			
			return matchRepository.save( new Match (username) );
		}); 
	}
	
	/**
	 * Returns a list of Matches based on list of users. If the Match isn't found
	 * in the database, it will be created and saved.
	 * 
	 * @param users
	 * @return
	 */
	private List<Match> retrieveOrAdd ( List <String> users ) {
		
		List<Match> retVal = new ArrayList<>();
		users.forEach( user -> {
			retVal.add( retrieveOrAdd(user) );
		});
		
		return retVal;
	}

}
