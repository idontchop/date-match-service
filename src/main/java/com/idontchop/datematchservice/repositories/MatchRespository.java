package com.idontchop.datematchservice.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.idontchop.datematchservice.entities.Match;

public interface MatchRespository extends MongoRepository<Match, String> {

	public List<String> findNameByNameIn(List<String> name);
	public Optional<Match> findByName(String user);
}
