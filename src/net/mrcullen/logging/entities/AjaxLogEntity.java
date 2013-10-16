package net.mrcullen.logging.entities;

import com.googlecode.objectify.annotation.Entity;


@Entity
public class AjaxLogEntity extends LogEntity {
	
	protected String requestUrl;
	protected String requestType;
	protected String requestParams;
	
	protected String responseCode;
	protected String responseText;
	protected String responseObject;
	
	protected AjaxLogEntity () {}
	
	public AjaxLogEntity (int entryLevel, String userEmail, String logMessage)
	{
		super (AJAX_LOG_ENTRY, entryLevel, userEmail, logMessage);
		requestUrl = requestType = requestParams = responseCode = responseText = responseObject = null;
	}
	
	public void setRequestInfo (String url, String type, String params)
	{
		requestUrl = url;
		requestType = type;
		requestParams = params;
	}
	
	public void setResponseInfo (String code, String text, String obj)
	{
		responseCode = code;
		responseText = text;
		responseObject = obj;
	}
	
	public String getRequestUrl () { return requestUrl; }
	public String getRequestType () { return requestType; }
	public String getRequestParams () { return requestParams; }
	
	public String getResponseCode () { return responseCode; }
	public String getResponseText () { return responseText; }
	public String getResponseObject () { return responseObject; }
}
