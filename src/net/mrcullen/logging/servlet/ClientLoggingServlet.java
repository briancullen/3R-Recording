package net.mrcullen.logging.servlet;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


import net.mrcullen.logging.entities.AjaxLogEntity;
import net.mrcullen.logging.entities.EventLogEntity;
import net.mrcullen.logging.entities.LogEntity;
import net.mrcullen.logging.entities.UserLogEntity;
import net.mrcullen.logging.entities.WindowLogEntity;


@SuppressWarnings("serial")
public class ClientLoggingServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(ClientLoggingServlet.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		String userEmail = null;
		UserService userService = UserServiceFactory.getUserService();
		User userInfo = userService.getCurrentUser();
		
		if (userInfo != null)
			userEmail = userInfo.getEmail();
		
		String requestPath = req.getPathInfo();
		StringTokenizer tokens = new StringTokenizer (requestPath, "/");
		if (!tokens.hasMoreTokens())
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		String action = tokens.nextToken();

		int level = -1;
		String levelParam = req.getParameter("LOG_LEVEL");
		if (levelParam != null)
		{
			for (int index = 0; index < LogEntity.LOG_ENTRY_LEVELS.length; index ++)
			{
				if (levelParam.equalsIgnoreCase(LogEntity.LOG_ENTRY_LEVELS[index]))
				{
					level = index;
					break;
				}
			}
		}
		
		if (level == -1)
		{
			level = LogEntity.ERROR_LEVEL;
		}
		
		String message = req.getParameter("LOG_MESSAGE");
		
		if (action.equalsIgnoreCase(LogEntity.LOG_ENTRY_TYPES[LogEntity.AJAX_LOG_ENTRY]))
		{
			AjaxLogEntity record = new AjaxLogEntity (level, userEmail, message);
			
			String url = req.getParameter("LOG_REQUESTURL");
			String type = req.getParameter("LOG_TYPE");
			String params = req.getParameter("LOG_PARAMS");
			record.setRequestInfo(url, type, params);
			
			String code = req.getParameter("LOG_CODE");
			String text = req.getParameter("LOG_TEXT");
			String obj = req.getParameter("LOG_OBJ");
			record.setResponseInfo(code, text, obj);
			ofy().save().entity(record);
			
		}
		else if (action.equalsIgnoreCase(LogEntity.LOG_ENTRY_TYPES[LogEntity.USER_LOG_ENTRY]))
		{
			String params = req.getParameter("LOG_PARAMS");
			UserLogEntity record = new UserLogEntity (level, userEmail, message, params);
			ofy().save().entity(record);
		}
		else if (action.equalsIgnoreCase(LogEntity.LOG_ENTRY_TYPES[LogEntity.WINDOW_LOG_ENTRY]))
		{
			int line = -1;
			String lineParam = req.getParameter("LOG_LINENUM");
			try {
				line = Integer.parseInt(lineParam);
			}
			catch (Exception ex) {}
			
			String sourceUrl = req.getParameter("LOG_SOURCEURL");
			WindowLogEntity record = new WindowLogEntity (level, userEmail, message, sourceUrl, line);
			ofy().save().entity(record);
		}
		else if (action.equalsIgnoreCase(LogEntity.LOG_ENTRY_TYPES[LogEntity.EVENT_LOG_ENTRY]))
		{
			EventLogEntity record = new EventLogEntity (userEmail, message);
			ofy().save().entity(record);
		}
		else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;			
		}
		
		resp.setStatus(HttpServletResponse.SC_OK);
	}
}
