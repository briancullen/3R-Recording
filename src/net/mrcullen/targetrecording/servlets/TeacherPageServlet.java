package net.mrcullen.targetrecording.servlets;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

import net.mrcullen.targetrecording.GsonService;
import net.mrcullen.targetrecording.UrlPathHelper;
import net.mrcullen.targetrecording.entities.FormEntity;
import net.mrcullen.targetrecording.entities.PupilEntity;
import net.mrcullen.targetrecording.process.FormInformation;
import net.mrcullen.targetrecording.process.PupilInformation;


@SuppressWarnings("serial")
public class TeacherPageServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(TeacherPageServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		UserService userService = UserServiceFactory.getUserService();
		req.setAttribute("LogoutURL", userService.createLogoutURL("/"));
		
		// req.setAttribute("FormInformation", GsonService.entityToJson(FormInformation.getForms()));
		RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/pages/teacher.jsp");
		dispatcher.forward(req, resp);
	}
}