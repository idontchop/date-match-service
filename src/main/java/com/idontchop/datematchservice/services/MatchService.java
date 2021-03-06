package com.idontchop.datematchservice.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
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
	private final String NAMEFIELD 		= 	"name";
	private final String TOFIELD		=	"to";
	private final String FROMFIELD		=	"from";
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	MatchRespository matchRepository;
	
	@Autowired
	MessageService messageService;
	
	/**
	 * Simply returns the requested users if found.
	 * 
	 * @param name
	 * @return
	 */
	public List<Match> getUser ( List<String> name ) {
		return matchRepository.findByNameIn(name);
	}
	
	/**
	 * Returns the Match document if found. If not found, returns empty.
	 * @param name
	 * @return
	 */
	public Match getUser (String name) {
		return matchRepository.findByName(name).orElse(new Match());
	}
	
	public Match getUserWithReduce (String name, List<String> potentials) {
		
		Match match = getUser(name);
		match.setFrom(match.getFrom().stream().filter( from -> potentials.contains(from)).collect(Collectors.toList()));
		match.setTo(match.getTo().stream().filter( to -> potentials.contains(to)).collect(Collectors.toList()));
		
		return match;
		
	}
	
	/**
	 * Returns the user's tofield
	 * 
	 * @param username
	 * @return
	 */
	public List<String> getUserMatch ( String username ) throws NoSuchElementException {
		
		return matchRepository.findByName(username)
			.orElseThrow()
			.getTo();
	}
	
	/**
	 * Returns the user's fromfield
	 * 
	 * @param username
	 * @return
	 * @throws NoSuchElementException
	 */
	public List<String> getUserIsMatch ( String username ) throws NoSuchElementException {
		
		return matchRepository.findByName(username)
				.orElseThrow()
				.getFrom();
	}
	
	/**
	 * Returns the user's connections. (users that exist in tofield and fromfield)
	 * 
	 * @param username
	 * @return
	 * @throws NoSuchElementException
	 */
	public List<String> getUserConnections ( String username ) throws NoSuchElementException {
		
		Match user = matchRepository.findByName(username)
				.orElseThrow();
		
		return user
				.getFrom()
				.stream()
				.filter( u -> user.getTo().contains(u) )
				.collect(Collectors.toList());
				
	}
	
	/**
	 * Will need a set of booleans that find matches of certain types
	 * when we don't want to return the whole list.
	 * 
	 *  unidirectional and bidirectional
	 *  
	 * @param name user document to check
	 * @param to user to search for
	 * @param checkTo if true, will check if exists in TO field
	 * @param checkFrom
	 * @return
	 */
	public boolean userHasMatch ( String name, String to, boolean checkTo, boolean checkFrom ) {
		
		Criteria toCriteria = Criteria.where(TOFIELD).all(to);
		Criteria fromCriteria = Criteria.where(FROMFIELD).all(to);
		
		Criteria searchCriteria = new Criteria();
		
		if ( checkTo && checkFrom ) { // match in both			
			searchCriteria.andOperator(toCriteria, fromCriteria);
		} else if ( checkTo ) {
			searchCriteria = toCriteria;
		} else if ( checkFrom ) {
			searchCriteria = fromCriteria;
		} else {
			throw new IllegalArgumentException ("MatchService userHasMatch received no arguments.");
		}
		
		Query query = new Query();
		
		query.addCriteria( searchCriteria );
		
		// If found at least one occurence, the match exists
		return mongoTemplate.count(query, Match.class) > 0;
		
	}
	
	/**
	 * Deletes the users. This doesn't check for database error.
	 * 
	 * @param name
	 */
	public void deleteUser ( List<String> name ) {
		
		name.forEach( user -> 
			matchRepository.deleteByName(user)
			);
		
	}
	
	/**
	 * Deletes the match, deletes from both user's records
	 * 
	 * If the match it is attemptign to delete doesn't exist, it creates the
	 * from user match.
	 */
	public Match deleteMatch(String from, String to) {
		
		matchRepository.findByName(to).ifPresent( m -> {
			m.deleteFrom(from);
			matchRepository.save(m);
		});
		
		Match fromMatch = matchRepository.findByName(from).orElse(new Match(from));
		fromMatch.deleteTo(to);
		
		// Sends 'matchDeleted' message
		messageService.sendDelete(from, to);
		
		return matchRepository.save(fromMatch);
	}
	
	/**
	 * Simple add from username to...
	 * 
	 * Two records will need to be updated.
	 * 
	 * Always returns the User without his matches for performance.
	 * 
	 * @param username
	 * @param to
	 * @return Match without from/to array
	 */
	public Match addMatch (String name, List<String> to ) {
		
		// Find ( and create if necessary ) user record
		Match user = retrieveOrAdd(name);	
		
		// Update reverse records
		updateOrAddTos(name, to);
		
		// update user to record
		Query query = new Query();
		query.addCriteria(Criteria.where(NAMEFIELD).is(name));		
		Update update = new Update().addToSet(TOFIELD).each(to);		
		mongoTemplate.updateFirst(query, update, Match.class);
		
		// Send new match message:
		// TODO: a bug exists here if the frontend sent a match that already existed.
		// Multiple notifications would be created (not a big deal until a bad actor)
		to.forEach( t -> {
			messageService.sendMatch(name, t);
		});
		// end message block
		
		return matchRepository.findByName(name).orElseThrow();
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
		// But this is likely never more than one at a time
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
		query.addCriteria(Criteria.where(NAMEFIELD).in(adds));
		Update update = new Update().addToSet(FROMFIELD, name);
		mongoTemplate.updateMulti(query, update, Match.class);
		
	}
	
	/**
	 * Retrieves the document from the db, or adds a new one.
	 * 
	 * @param username
	 * @return
	 */
	private Match retrieveOrAdd (String username ) {
		 
		Optional<Match> match = matchRepository.findNameByName(username);
		
		return match.orElseGet( () -> matchRepository.save( new Match (username) )); 
	}
	
	/**
	 * Returns a list of Matches based on list of users. If the Match isn't found
	 * in the database, it will be created and saved.
	 * 
	 * @param users
	 * @return
	 *//* unused
	private List<Match> retrieveOrAdd ( List <String> users ) {
		
		List<Match> retVal = new ArrayList<>();
		users.forEach( user -> {
			retVal.add( retrieveOrAdd(user) );
		});
		
		return retVal;
	}*/

}
