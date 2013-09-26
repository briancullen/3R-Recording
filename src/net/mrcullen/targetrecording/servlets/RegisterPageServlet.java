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
public class RegisterPageServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(RegisterPageServlet.class.getName());
    
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		if (!UrlPathHelper.isPathEmpty(req.getPathInfo()))
		{
			log.warning("[POST] Unexpected path in URL (" + req.getPathInfo() + ")");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String newUserEmail = req.getParameter("UserEmail");
		if (newUserEmail == null)
		{
			log.warning("[POST] User email not specified as part of request.");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String newUserName = req.getParameter("UserName");
		if ((newUserName == null) || (newUserName.isEmpty()))
			newUserName = newUserEmail;
		
		
		Key<FormEntity> newUserFormKey = UrlPathHelper.getKeyFromPath(req.getParameter("UserFormKey"), FormEntity.class.getSimpleName());
		
		if ((newUserFormKey == null) || (FormInformation.getForm(newUserFormKey) == null))
		{
			log.warning("[POST] Invalid or malformed form key passed to server.");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;				
		}
		
		PupilEntity newPupil = new PupilEntity (newUserEmail, newUserName, Ref.create(newUserFormKey));
		Key<PupilEntity> key = PupilInformation.savePupil(newPupil);
		
		String json = "[ ]";
		if (key != null)
			json = GsonService.keyToJson(key);
		else log.severe("[POST] No key returned on attempt to save target entity to database");
		
		resp.getWriter().print(json);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		UserService userService = UserServiceFactory.getUserService();
		User userInfo = userService.getCurrentUser();
		
		if (userInfo == null)
		{
			log.severe("[GET] No logged in user - shouldn't be able to happen!");
			// Shouldn't be able to happen!
			((HttpServletResponse)resp).sendRedirect(userService.createLoginURL(((HttpServletRequest)req).getRequestURL().toString()));
		    return;
		}
		
		String userEmail = userInfo.getEmail();
		req.setAttribute("UserEmail", userEmail);

		PupilEntity pupil = PupilInformation.getPupil(userEmail);
		if (pupil != null)
		{
			log.warning("[GET] Pupil already registered - denying access.");
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		req.setAttribute("FormInformation", GsonService.entityToJson(FormInformation.getForms()));
		RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/pages/register.jsp");
		dispatcher.forward(req, resp);
	}
}
