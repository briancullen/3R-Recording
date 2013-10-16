package net.mrcullen.logging.entities;

import com.googlecode.objectify.annotation.Entity;


@Entity
public class WindowLogEntity extends LogEntity {
	
	protected String sourceUrl;
	protected int lineNumber;
	
	protected WindowLogEntity () {}
	public WindowLogEntity (int entryLevel, String userEmail, String logMessage)
	{
		this (entryLevel, userEmail, logMessage, null, -1);
	}

	public WindowLogEntity (int entryLevel, String userEmail, String logMessage, String sourceUrl, int lineNumber)
	{
		super (WINDOW_LOG_ENTRY, entryLevel, userEmail, logMessage);
		setSourceUrl(sourceUrl);
		setLineNumber(lineNumber);
	}
	
	public String getSourceUrl () { return sourceUrl; }
	public int getLineNumber() { return lineNumber; }
	
	public boolean setSourceUrl (String url)
	{
		if (url == null)
		{
			sourceUrl = "";
			return false;
		}
		else {
			sourceUrl = url;
			return true;
		}
	}
	
	public boolean setLineNumber (int number)
	{
		lineNumber = number;
		return true;
	}
}
