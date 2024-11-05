package com.incture.bupa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class WorkflowEventsConsumer {
	private Logger logger = LoggerFactory.getLogger(WorkflowEventsConsumer.class);
	
}
