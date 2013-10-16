package net.mrcullen.logging.entities;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public abstract class LogEntity {
	
	public final static int WINDOW_LOG_ENTRY = 0;
	public final static int AJAX_LOG_ENTRY = 1;
	public final static int USER_LOG_ENTRY = 2;
	public final static int EVENT_LOG_ENTRY = 3;
	
	public final static String[] LOG_ENTRY_TYPES = { "WINDOW", "AJAX", "USER", "EVENT" };
	
	public final static int INFO_LEVEL = 0;
	public final static int WARNING_LEVEL = 1;
	public final static int ERROR_LEVEL = 2;
	public final static int CRITICAL_LEVEL = 3;
	
	public final static String[] LOG_ENTRY_LEVELS = { "INFORMATION", "WARNING", "ERROR", "CRITICAL" };
	
	// Flags indicating the level and type of the event logged.
	@Index protected int logEntryType;
	@Index protected int logEntryLevel;

	// ID of the record, the time and the user the entry is for
	// these fields are all mandatory and could be filled in on
	// the server if necessary.
	@Id protected Long logId;
	@Index protected Date timeStamp;
	@Index protected String userEmail;
	
	// Textual description of the error and how it was caused.
	protected String logMessage;
		
	protected LogEntity() {}
	
	public LogEntity (int entryType, int entryLevel, String userEmail, String logMessage)
	{
		timeStamp = new Date ();
		
		if ((entryType < 0) || (entryType > 3))
		{
			entryType = USER_LOG_ENTRY;
		}
		
		if ((entryLevel < 0) || (entryLevel > 3))
		{
			entryLevel = ERROR_LEVEL;
		}
		
		logEntryType = entryType;
		logEntryLevel = entryLevel;
		
		setUserEmail(userEmail);
		setLogMessage(logMessage);
	}
	
	public int getLogEntryType () { return logEntryType; }
	public int getLogEntryLevel () { return logEntryLevel; }
	
	public String getLogEntryTypeString () { return LOG_ENTRY_TYPES [logEntryType]; }
	public String getLogEntryLevelString () { return LOG_ENTRY_LEVELS [logEntryLevel]; }
	
	public Date getTimeStamp () { return timeStamp; }
	public String getUserEmail () { return userEmail; }
	public String getLogMessage () { return logMessage; }

	public boolean setUserEmail(String email) {
		if (email == null)
		{
			userEmail = "";
		}
		else {
			userEmail = email;
		}
		return true;
	}
	
	public boolean setLogMessage (String message)
	{
		if (message == null)
		{
			logMessage = "";
		}
		else {
			logMessage = message;
		}
		return true;
	}
}
