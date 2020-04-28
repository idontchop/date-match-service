package com.idontchop.datematchservice.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.idontchop.datematchservice.entities.Match;

public interface MatchRespository extends MongoRepository<Match, String> {

	public List<Match> findByNameIn(List<String> name);
	
	@Query ( fields = "{name: 1}")
	public List<Match> findNameByNameIn(List<String> name);
	public Optional<Match> findByName(String name);
	public Optional<Match> findNameByName(String user);
	public void deleteByName(String user);
}
