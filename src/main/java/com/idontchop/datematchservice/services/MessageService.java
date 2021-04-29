package com.idontchop.datematchservice.services;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Service;

import com.idontchop.datematchservice.dtos.MatchMessageDto;

@Service
public class MessageService {
	
	Logger logger = LoggerFactory.getLogger(MessageService.class);
	
	@Value("${spring.application.topic}")
	private String topic;
	
	@Value("${spring.kafka.consumer.bootstrap-servers}")
	private String bootstrap;
			

	private void send(String from, String to, String eventType) {
		
		// record
		ProducerRecord<String, MatchMessageDto> record = new ProducerRecord<>(topic, from, new MatchMessageDto(from,to));

		// header
		record.headers().add( new RecordHeader("event-type", eventType.getBytes()));
		
		// send message
		KafkaProducer<String, MatchMessageDto> producer = getProducer();
		producer.send(record, (recordMetaData, e) -> sendCompletion(recordMetaData,e, record) );
		
		// close to force send
		producer.close();			
		
	}
	
	public void sendMatch(String from, String to) {
		send(from,to,"matchCreated");
	}
	
	public void sendDelete(String from, String to) {
		send(from,to,"matchDeleted");
		
	}
	
	
	private void sendCompletion ( RecordMetadata recordMetaData, Exception e, ProducerRecord<String, MatchMessageDto> record) {
		
		if ( e == null ) {
			logger.info("(" + topic + ":" +
					recordMetaData.offset() + "-" +
					"Partition: " + recordMetaData.partition() +
					") Match Message sent. " + record.value().getFromId() + "->" + record.value().getToId());
			
		} else {
			logger.error("Error sending: " + e.getLocalizedMessage());
		}
	}
	
	private KafkaProducer<String, MatchMessageDto> getProducer() {
		
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

		// create producer
		KafkaProducer<String, MatchMessageDto> producer = new KafkaProducer<>(properties);
		
		return producer;

	}

}
