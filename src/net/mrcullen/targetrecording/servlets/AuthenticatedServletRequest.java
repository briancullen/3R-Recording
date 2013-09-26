package net.mrcullen.targetrecording.servlets;

import java.io.IOException;
import java.security.Permissions;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.KeyFactory;
import com.googlecode.objectify.Key;

import net.mrcullen.targetrecording.OfyService;
import net.mrcullen.targetrecording.UrlPathHelper;

@SuppressWarnings("serial")
public abstract class AuthenticatedServletRequest extends HttpServlet {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(AuthenticatedServletRequest.class.getName());
	
	public static final String ALL_PERMISSION = "ALL";
	public static final String OWN_PERMISSION = "OWN";
	public static final String PUPIL_PERMISSION = "PUPIL";
	public static final String TEACHER_PERMISSION = "TEACHER";
	public static final String ADMIN_PERMISSION = "ADMIN";
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		if (checkAccess(req))
			doAuthenticatedPost(req, resp);
		else resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}
	
	public void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		if (checkAccess(req))
			doAuthenticatedPut(req, resp);
		else resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	public void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		if (checkAccess(req))
			doAuthenticatedDelete(req, resp);
		else resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		if (checkAccess(req))
			doAuthenticatedGet(req, resp);
		else resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}
	
	public abstract void doAuthenticatedPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException;

	public abstract void doAuthenticatedPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException;

	public abstract void doAuthenticatedDelete(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException;

	public abstract void doAuthenticatedGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException;

	public abstract Set<String> getRequiredPermission (String method);

	public boolean checkAccess (HttpServletRequest request)
	{
		Set<String> permissions = getRequiredPermission(request.getMethod());
		String entityKeyString = UrlPathHelper.getNameFromPath(request.getPathInfo());
		
		if (permissions.contains(ALL_PERMISSION))
		{
			return true;
		}
		
		if ((permissions.contains(OWN_PERMISSION)) && (entityKeyString != null))
		{
			String authenticatedUserKeyString = (String) request.getAttribute("UserEntityKey");
			
			if (entityKeyString.equals(authenticatedUserKeyString))
				return true;
			
			Key authenticatedUserKey = Key.create(KeyFactory.stringToKey(authenticatedUserKeyString));
			Key entityKey = Key.create(KeyFactory.stringToKey(entityKeyString)).getParent();
			
			while (entityKey != null)
			{
				if (entityKey.equals(authenticatedUserKey))
					return true;
				
				entityKey = entityKey.getParent();
			}
			
			log.warning("[" + request.getMethod() + "] Refused as entity not related to user.");
		}
		
		if (permissions.contains(PUPIL_PERMISSION))
		{
			if (!(((Boolean)request.getAttribute("TeacherType")).booleanValue()))
				return true;
			
			log.warning("[" + request.getMethod() + "] Refused as user is not a pupil type.");
		}
		
		if (permissions.contains(TEACHER_PERMISSION))
		{
			if (((Boolean)request.getAttribute("TeacherType")).booleanValue())
				return true;
			
			log.warning("[" + request.getMethod() + "] Refused as user is not a teacher type.");
		}
		
		if (permissions.contains(ADMIN_PERMISSION))
		{
			if (((Boolean)request.getAttribute("UserAdmin")).booleanValue())
				return true;
			
			log.warning("[" + request.getMethod() + "] Refused as user is not an admin.");
		}
		
		log.warning("[" + request.getMethod() + "] Refused as no permissions match.");
		return false;
	}	
}
