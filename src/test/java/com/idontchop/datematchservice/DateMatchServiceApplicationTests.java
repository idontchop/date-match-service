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
import com.idontchop.datematchservice.dtos.MatchDto;
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
	

	List<String> tos = List.of("22","0","23","24","10","Nate","nada","wtf");
	
	//Arrays.asList(match(eq("name", "username")), 
	//project(fields(excludeId(), computed("reduce", eq("$setIntersection", Arrays.asList(eq("$concatArrays", Arrays.asList("$to", "$from")), Arrays.asList("0")))))))
	
	

	@Test
	public void testUserHasMatch() {
		
		String username = "username";
		
		assertTrue (matchService.userHasMatch(username, "22", true, false) );

	}
	
	
	public void aggregation () {
		
		ObjectMapper mapper = new ObjectMapper();
		
		String u = "username";
		
		MatchDto m = reduceService.findDifference(u, tos);
		
		// to keep a useless reduce field in the DB, I see one of two options:
		// 1) extend the Match class to contain a reduce field
		// 2) just map reduce to to field and only return to field.
		
		assertTrue (m != null);
		//System.out.println(m.get(0).getTo().size() == 2);
		assertEquals ( 2, m.getReduce().size());
		assertTrue ( m.getReduce().get(0).equals( "nada"));
		assertTrue ( m.getReduce().get(1).equals("wtf"));
		
		
		
		try {
			System.out.println( mapper.writeValueAsString(m.getReduce()) );
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		m = reduceService.findIntersection(u, tos, true);
		assertEquals (6, m.getReduce().size());
		
		List<String> tof = List.of("22","username2");
		List<String> tofc = List.of("conn", "conn2");
		m = reduceService.findIntersection(u, tof, true);
		assertEquals (1, m.getReduce().size());
		
		m = reduceService.findIntersection(u, tof, false);
		assertEquals (1, m.getReduce().size());
		
		m = reduceService.findFullIntersection(u, tos);
		assertEquals (0, m.getReduce().size());
		
		m = reduceService.findFullIntersection(u, tofc);
		assertEquals (2, m.getReduce().size());
		
		
		
	}
	
	
	public void testAddMatch() {
		
		String user = "username";
		String user2 = "username2";
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
