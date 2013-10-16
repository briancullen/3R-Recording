package net.mrcullen.logging.entities;


import com.googlecode.objectify.annotation.Entity;


@Entity
public class UserLogEntity extends LogEntity {
	
	protected String logParams;
	
	protected UserLogEntity () {}
	public UserLogEntity (int entryLevel, String userEmail, String logMessage, String params)
	{
		super (USER_LOG_ENTRY, entryLevel, userEmail, logMessage);
		setParams (params);
	}
	
	public String getParams () { return logParams; }
	public boolean setParams (String params) {
		if (params == null || params.length() < 2)
		{
			logParams = "[]";
			return false;
		}
		else {
			logParams = params;
			return true;
		}
	}
}
