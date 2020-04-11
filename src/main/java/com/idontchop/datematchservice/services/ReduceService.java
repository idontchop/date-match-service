package com.idontchop.datematchservice.services;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.idontchop.datematchservice.dtos.MatchDto;
import com.idontchop.datematchservice.entities.Match;
import com.idontchop.datematchservice.repositories.MatchRespository;

@Service
public class ReduceService {
	
	// Database fields in the Match Document
	// TODO: set these to property values
	private final String NAMEFIELD 		= 	"name";
	private final String TOFIELD		=	"to";
	private final String FROMFIELD		=	"from";
	
	@Value ("${spring.application.type}")
	private String matchType;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	MatchRespository matchRepository;
	
	private MatchDto agg ( MatchOperation matchStage, ProjectionOperation projectStage ) throws IndexOutOfBoundsException {
		
		Aggregation agg = Aggregation.newAggregation(matchStage, projectStage);		
		AggregationResults<MatchDto> out = mongoTemplate.aggregate(agg, "match", MatchDto.class);
		return out.getMappedResults().get(0);
	}
	
	/**
	 * findDifference. See MainController.java
	 * 
	 * @param name
	 * @param potentials
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public MatchDto findDifference (String name, List<String> potentials ) throws IndexOutOfBoundsException {
		
		MatchOperation matchStage = Aggregation.match(Criteria.where(NAMEFIELD).is(name));
		ProjectionOperation projectStage = Aggregation.project()
				.and( c ->  new Document ( "$setDifference", 
						Arrays.asList( potentials,
								new Document ("$concatArrays", Arrays.asList("$" + TOFIELD,"$" + FROMFIELD))
								)))
				.as("reduce");		

		return agg(matchStage, projectStage);
	}
	
	/**
	 * find Intersection. findDifference. See MainController.java
	 * 
	 * @param name
	 * @param potentials
	 * @param isTo true to use TOFIELD
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public MatchDto findIntersection ( String name, List<String> potentials, boolean isTo ) throws IndexOutOfBoundsException {
		
		final String FIELD = isTo ? TOFIELD : FROMFIELD;
		
		MatchOperation matchStage = Aggregation.match(Criteria.where(NAMEFIELD).is(name));		
		ProjectionOperation projectStage = Aggregation.project()
				.and ( c -> new Document ( "$setIntersection",
						Arrays.asList("$" + FIELD, potentials)
						))
				.as("reduce");
		
		return agg(matchStage, projectStage);
	}
	
	/**
	 * find 3 set intersection, uses TO and FROM field. See MainController.java
	 * 
	 * @param name
	 * @param potentials
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public MatchDto findFullIntersection ( String name, List<String> potentials) throws IndexOutOfBoundsException {
		
		MatchOperation matchStage = Aggregation.match(Criteria.where(NAMEFIELD).is(name));
		ProjectionOperation projectStage = Aggregation.project()
				.and( c-> new Document ( "$setIntersection",
						Arrays.asList(potentials,
								"$" + TOFIELD, "$" + FROMFIELD
								)))
				.as("reduce");
		
		return agg(matchStage, projectStage);					
	}

}
