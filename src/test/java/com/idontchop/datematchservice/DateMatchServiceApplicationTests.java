package com.idontchop.datematchservice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.idontchop.datematchservice.repositories.MatchRespository;

@SpringBootTest
class DateMatchServiceApplicationTests {

	@Autowired
	MatchRespository matchRepository;
	
	@Test
	void contextLoads() {
	}
	
	@Test
	public void testFindName () {
		
		List<String> tos = List.of("22","1","23","24");
		List<String> after = matchRepository.findNameByNameIn(tos);
		
		assertEquals(3,after.size());
		
	}

}
