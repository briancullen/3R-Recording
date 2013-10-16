package net.mrcullen.logging.entities;

import com.googlecode.objectify.annotation.Entity;


@Entity
public class EventLogEntity extends LogEntity {
	
	protected EventLogEntity () {}
	
	public EventLogEntity (String userEmail, String logMessage)
	{
		super (EVENT_LOG_ENTRY, INFO_LEVEL, userEmail, logMessage);
	}
}
