package net.mrcullen.targetrecording.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;

import net.mrcullen.targetrecording.GsonService;
import net.mrcullen.targetrecording.entities.FormEntity;
import net.mrcullen.targetrecording.entities.SubjectEntity;
import net.mrcullen.targetrecording.entities.TeacherEntity;
import net.mrcullen.targetrecording.process.FormInformation;
import net.mrcullen.targetrecording.process.SubjectInformation;
import net.mrcullen.targetrecording.process.TeacherInformation;

@SuppressWarnings("serial")
public class ResetServlet extends HttpServlet {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ResetServlet.class.getName());


	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
	}

	public void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
	}

	
	public void doDelete (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
	}
	
	public void doGet (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		// If there are no teachers already in the database then make one.
		if (TeacherInformation.getTeachers().size() == 0)
		{
			TeacherEntity teacher = new TeacherEntity ("bcullen@rossettlearning.co.uk", "Mr B Cullen", "cl", true);
			Key<TeacherEntity> key = TeacherInformation.saveTeacher(teacher);
			resp.getWriter().print(GsonService.keyToJson(key));
			
			FormEntity form = new FormEntity ("EV", 2010);
			FormInformation.saveForm(form);
			
			FormEntity form1 = new FormEntity ("CL", 2008);
			FormInformation.saveForm(form1);
			
			FormEntity form2 = new FormEntity ("CT", 2012);
			FormInformation.saveForm(form2);
			
			SubjectEntity subject = new SubjectEntity ("Cambridge Tech", true);
			SubjectInformation.saveSubject(subject);
			
			subject = new SubjectEntity ("English", false);
			SubjectInformation.saveSubject(subject);
			
			subject = new SubjectEntity ("Biology", false);
			SubjectInformation.saveSubject(subject);
			
			subject = new SubjectEntity ("BTEC Sport", true);
			SubjectInformation.saveSubject(subject);
		}
	}	
}
