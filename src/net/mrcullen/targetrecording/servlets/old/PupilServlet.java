package net.mrcullen.targetrecording.servlets.old;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import net.mrcullen.targetrecording.GsonService;
import net.mrcullen.targetrecording.entities.FormEntity;
import net.mrcullen.targetrecording.entities.PersonEntity;
import net.mrcullen.targetrecording.entities.PupilEntity;
import net.mrcullen.targetrecording.entities.SubjectEntity;
import net.mrcullen.targetrecording.entities.TargetProgressEntity;
import net.mrcullen.targetrecording.entities.TeacherEntity;
import net.mrcullen.targetrecording.process.PupilInformation;
import net.mrcullen.targetrecording.process.SubjectInformation;
import net.mrcullen.targetrecording.process.PupilTargetInformation;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;


@SuppressWarnings("serial")
public class PupilServlet extends HttpServlet {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(PupilServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		UserService userService = UserServiceFactory.getUserService();
		User userInfo = userService.getCurrentUser();
		
		/* if (userInfo == null)
		{
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			resp.getOutputStream().println("<H1>You are not authorised to access this resource.</H1>");
			return;
		}
		
		String userEmail = userInfo.getEmail(); */
		String userEmail = "test@rossettlearning.co.uk";
		
		resp.getWriter().println(GsonService.gson.toJson(PupilInformation.getPupil(Key.create(PupilEntity.class, userEmail))));
	}
}
