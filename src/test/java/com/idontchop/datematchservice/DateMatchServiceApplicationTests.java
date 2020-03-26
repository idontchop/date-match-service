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

import com.idontchop.datematchservice.entities.Match;
import com.idontchop.datematchservice.repositories.MatchRespository;
import com.idontchop.datematchservice.services.MatchService;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class DateMatchServiceApplicationTests {

	@Autowired
	MatchRespository matchRepository;
	
	@Autowired
	MatchService matchService;
	
	@Test
	void contextLoads() {
	}
	

	List<String> tos = List.of("22","0","23","24","10","Nate");
	
	@Test	
	public void testAddMatch() {
		
		assertTrue(tos.size() == 6);
		Match match = matchService.addMatch("username", tos);
		assertTrue ( match.getName() == "username");
		List<String> ntos = new ArrayList<>(tos);
		ntos.add("username");
		match = matchService.addMatch("username2", ntos);
		assertTrue ( match.getName() == "username2");
		System.out.println(match);
		
	}
	
	@Test
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
	
	@Test
	@Order(2)
	public void testFind () {
		
		List<Match> m = matchRepository.findByNameIn(tos);
		
		assertEquals(6,tos.size());
		assertEquals(2,m.size());
		
		List<Match> t = matchRepository.findNameByNameIn(tos);
		
		t.forEach(tt -> System.out.println(tt));
		assertEquals(2,t.size());
	}
	
	@Test
	@Order(3)
	public void testUpdate ( ) {
		
		matchService.updateOrAddTos("newUser2", tos);
		matchService.updateOrAddTos("newUser3", tos);
		assertEquals(6,tos.size());
		
		List<Match> after = matchRepository.findNameByNameIn(tos);		
		assertEquals(6,after.size());
		
	}

}
