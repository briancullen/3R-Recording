package net.mrcullen.coursework;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import net.mrcullen.coursework.entities.*;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
    static {
    	//ObjectifyService.register(PersonEntity.class);
    }
    
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		//TeacherEntity teacher = new TeacherEntity ("aa@bb.com", "Mr A Brown", true);
		//ofy().save().entities(teacher);
		
		//PupilEntity loaded = ofy().load().key(Key.create(PupilEntity.class, "bb@cc.com")).get();
		
		
		UserService userService = UserServiceFactory.getUserService();
		// User user = userService.getCurrentUser();
		
		// req.setAttribute("user", user.getName());
		req.setAttribute("loginURL", userService.createLoginURL("/"));
		req.setAttribute("logoutURL", userService.createLogoutURL("/"));
		
		RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/pages/login.jsp");
		dispatcher.forward(req, resp);
		
	}
}
