package com.idontchop.datematchservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.idontchop.datematchservice.entities.Match;
import com.idontchop.datematchservice.repositories.MatchRespository;
import com.idontchop.datematchservice.services.MatchService;

@SpringBootTest
class DateMatchServiceApplicationTests {

	@Autowired
	MatchRespository matchRepository;
	
	@Autowired
	MatchService matchService;
	
	@Test
	void contextLoads() {
	}
	

	List<String> tos = List.of("22","0","23","24");
	@Test
	public void testdb () {
		
		for ( int c = 0; c < 20; c = c+2) {
			Match match = new Match(String.valueOf(c));
			matchRepository.save(match);
		}
		
		List<Match> users = matchRepository.findAll();
		assertTrue (users.size() > 0);
		
		users.forEach( u -> System.out.println(u.getName()));
		
	}
	
	@Test
	public void testFind () {
		
		List<String> t = matchRepository.findNameByNameIn(tos);
		
		t.forEach(tt -> System.out.println(tt));
		assertEquals(4,t.size());
	}
	
	@Test
	public void testUpdate ( ) {
		
		
		
		matchService.updateOrAddTos("newUser", tos);
		assertEquals(4,tos.size());
		List<String> after = matchRepository.findNameByNameIn(tos);
		
		assertEquals(0,after.size());
		
	}

}
