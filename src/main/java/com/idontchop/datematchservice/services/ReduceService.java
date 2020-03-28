package com.idontchop.datematchservice.services;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SetOperators.SetIntersection;
import org.springframework.data.mongodb.core.aggregation.SetOperators.SetDifference;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.idontchop.datematchservice.entities.Match;
import com.idontchop.datematchservice.repositories.MatchRespository;

@Service
public class ReduceService {
	
	// Database fields in the Match Document
	// TODO: set these to property values
	private final String NAMEFIELD 		= 	"name";
	private final String TOFIELD		=	"to";
	private final String FROMFIELD		=	"from";
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	MatchRespository matchRepository;
	
	public List<Match> findDifference (String name, List<String> potentials ) {
		
		// We need to pass the potentials into mongo db and someone have it find
		// the difference and return just the array
		// planning ahead for possibility of a user have thousands of records
		// we can't return the array
		// other possibility is checking each potential one by one
		MatchOperation matchStage = Aggregation.match(Criteria.where(NAMEFIELD).is(name));
		ProjectionOperation projectStage = Aggregation.project()
				.and( c ->  new Document ("$setIntersection", Arrays.asList( new Document ("$concatArrays", Arrays.asList("$to","$from")), potentials)))
				.as("reduce");		

		Aggregation agg = Aggregation.newAggregation(matchStage, projectStage);
		
		AggregationResults<Match> out = mongoTemplate.aggregate(agg, "match", Match.class);
		return out.getMappedResults();
	}

}
