package net.mrcullen.targetrecording.filters;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.mrcullen.targetrecording.GsonService;
import net.mrcullen.targetrecording.entities.PupilEntity;
import net.mrcullen.targetrecording.entities.TeacherEntity;
import net.mrcullen.targetrecording.process.FormInformation;
import net.mrcullen.targetrecording.process.PupilInformation;
import net.mrcullen.targetrecording.process.TeacherInformation;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;

public class UserAuthentication implements Filter {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(UserAuthentication.class.getName());
	protected FilterConfig filterConfig;
	
	@Override
	public void destroy() {	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException
	{
		UserService userService = UserServiceFactory.getUserService();
		User userInfo = userService.getCurrentUser();
		
		String requiredRole = filterConfig.getInitParameter("RequiredRole");
		String userEmail = userInfo.getEmail();
		req.setAttribute("UserEmail", userEmail);
		
		
		PupilEntity pupil = PupilInformation.getPupil(userEmail);
		TeacherEntity teacher = null;
		if (pupil != null)
		{
			req.setAttribute("UserEntity", pupil);
			req.setAttribute("UserEntityKey", KeyFactory.keyToString(Key.create(pupil).getRaw()));
			req.setAttribute("UserAdmin", Boolean.FALSE);
			req.setAttribute("TeacherType", Boolean.FALSE);	
		}
		else {
			teacher = TeacherInformation.getTeacher(userEmail);
			if (teacher != null)
			{
				req.setAttribute("UserEntity", teacher);
				req.setAttribute("UserEntityKey", KeyFactory.keyToString(Key.create(teacher).getRaw()));
				req.setAttribute("UserAdmin", Boolean.valueOf(teacher.isAdmin()));
				req.setAttribute("TeacherType", Boolean.TRUE);
			}
		}

		if (requiredRole != null)
		{
			if ((requiredRole.equalsIgnoreCase("teacher")) && (teacher == null))
			{
				((HttpServletResponse)resp).sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
	
			if ((requiredRole.equalsIgnoreCase("pupil")) && (pupil == null))
			{
				// not registered need to kick to registration page.
				((HttpServletResponse)resp).sendRedirect(filterConfig.getInitParameter("RegistrationPage"));
				return;
			}
		} 
		chain.doFilter(req, resp);

	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		// TODO Auto-generated method stub
		filterConfig = config;
	}

}
