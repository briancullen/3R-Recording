package net.mrcullen.targetrecording.servlets.old;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import net.mrcullen.targetrecording.test.PopulateData;


@SuppressWarnings("serial")
public class StudentServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(StudentServlet.class.getName());
    
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		// PopulateData.populateData();
		RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/pages/student.jsp");
		dispatcher.forward(req, resp);
	}
}
