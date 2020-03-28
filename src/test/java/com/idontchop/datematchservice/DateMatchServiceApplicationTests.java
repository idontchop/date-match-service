package com.idontchop.datematchservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idontchop.datematchservice.entities.Match;
import com.idontchop.datematchservice.repositories.MatchRespository;
import com.idontchop.datematchservice.services.MatchService;
import com.idontchop.datematchservice.services.ReduceService;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class DateMatchServiceApplicationTests {

	@Autowired
	MatchRespository matchRepository;
	
	@Autowired
	MatchService matchService;
	
	@Autowired
	ReduceService reduceService;
	
	@Test
	void contextLoads() {
	}
	

	List<String> tos = List.of("22","0","23","24","10","Nate","nada");
	
	//Arrays.asList(match(eq("name", "username")), 
	//project(fields(excludeId(), computed("reduce", eq("$setIntersection", Arrays.asList(eq("$concatArrays", Arrays.asList("$to", "$from")), Arrays.asList("0")))))))
	
	@Test
	public void aggregation () {
		
		ObjectMapper mapper = new ObjectMapper();
		
		List<Match> m = reduceService.findDifference("username", tos);
		
		// to keep a useless reduce field in the DB, I see one of two options:
		// 1) extend the Match class to contain a reduce field
		// 2) just map reduce to to field and only return to field.
		
		assertTrue (m.size() > 0);
		//System.out.println(m.get(0).getTo().size() == 2);
		assertEquals ( 6, m.get(0).getReduce().size());
		assertTrue ( m.get(0).getReduce().get(0).equals( "0"));
		assertTrue ( m.get(0).getReduce().get(1).equals("10"));
		
		
		m.forEach( e -> {
			try {
				System.out.println( mapper.writeValueAsString(e) );
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
	}
	
	@Test
	public void testAddMatch() {
		
		String user = "Nada2";
		String user2 = "Nada3";
		Match match = matchService.addMatch(user, tos);
		assertTrue ( match.getName().equals( user ));
		List<String> ntos = new ArrayList<>(tos);
		ntos.add(user);
		match = matchService.addMatch(user2, ntos);
		assertTrue ( match.getName().equals(user2));
		System.out.println(match);
		
	}
	
	
	@Order(1)
	public void testdb () {
		
		matchRepository.deleteAll();
		
		for ( int c = 0; c < 20; c = c+2) {
			Match match = new Match(String.valueOf(c));
			matchRepository.save(match);
		}
		
		List<Match> users = matchRepository.findAll();
		assertTrue (users.size() > 0);
		
		//users.forEach( u -> System.out.println(u.getName()));
		List<String> adds = matchRepository.findNameByNameIn(tos)
				.stream().map(Match::getName).collect(Collectors.toList());
		
		adds.forEach( aa -> System.out.println(aa));
		assertEquals(2,adds.size());
		
	}
	
	
	@Order(2)
	public void testFind () {
		
		List<Match> m = matchRepository.findByNameIn(tos);
		
		assertEquals(6,tos.size());
		assertEquals(2,m.size());
		
		List<Match> t = matchRepository.findNameByNameIn(tos);
		
		t.forEach(tt -> System.out.println(tt));
		assertEquals(2,t.size());
	}
	
	
	@Order(3)
	public void testUpdate ( ) {
		
		matchService.updateOrAddTos("newUser2", tos);
		matchService.updateOrAddTos("newUser3", tos);
		assertEquals(6,tos.size());
		
		List<Match> after = matchRepository.findNameByNameIn(tos);		
		assertEquals(6,after.size());
		
	}

}
