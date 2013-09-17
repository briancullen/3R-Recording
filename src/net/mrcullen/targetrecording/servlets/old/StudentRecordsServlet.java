package net.mrcullen.targetrecording.servlets.old;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
import net.mrcullen.targetrecording.process.PupilTargetInformation;
import net.mrcullen.targetrecording.test.PopulateData;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;


@SuppressWarnings("serial")
public class StudentRecordsServlet extends javax.servlet.http.HttpServlet {
	private static final Logger log = Logger.getLogger(StudentRecordsServlet.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		String path = req.getPathInfo();
		if (path != null && (!path.isEmpty()))
		{
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
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
		
		Ref<PupilEntity> pupil = Ref.create(Key.create(PupilEntity.class, userEmail));
		
		String subjectId = req.getParameter("recordDialogSubject");
		Ref<SubjectEntity> subject = Ref.create(Key.create(SubjectEntity.class, Long.parseLong(subjectId)));
		
		String year = req.getParameter("recordDialogYear");
		TargetProgressEntity newTarget = null;
		
		String targetId = req.getParameter("recordDialogTargetId"); 
		if ((targetId != null) && (!targetId.isEmpty()))
		{
			newTarget = new TargetProgressEntity (Long.valueOf(req.getParameter("recordDialogTargetId")),
					pupil, subject, Integer.parseInt(year));
		}
		else newTarget = new TargetProgressEntity (pupil, subject, Integer.parseInt(year));
		
		newTarget.setTargetGrades(req.getParameter("recordDialogMinTargetGrade"),
								req.getParameter("recordDialogExpectedTargetGrade"),
								req.getParameter("recordDialogAspirationalTargetGrade"));
		

		newTarget.setRecordType(req.getParameter("recordDialogTargetType"));
		newTarget.setLevel(Integer.parseInt(req.getParameter("recordDialogCurrentLevel")));
		newTarget.setTarget(req.getParameter("recordDialogTarget"));
		
		
		PupilTargetInformation.saveTarget(newTarget);
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		// PopulateData.populateRecords();
		
		UserService userService = UserServiceFactory.getUserService();
		User userInfo = userService.getCurrentUser();
		
		/* if (userInfo == null)
		{
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String userEmail = userInfo.getEmail(); */
		String userEmail = "test@rossettlearning.co.uk";
		
		Key<PupilEntity> pupil = Key.create(PupilEntity.class, userEmail);
		PupilTargetInformation info = new PupilTargetInformation (Ref.create(pupil));
		
		String path = req.getPathInfo();
		
		if (path == null || path.isEmpty())
		{
			String recordType = req.getParameter("RECORD_TYPE");
			
			int recordYear = -1;
			String yearParameter = req.getParameter("RECORD_YEAR");
			
			if (yearParameter != null)
			{
				try {
					recordYear = Integer.parseInt(yearParameter);
				}
				catch (Exception ex) {  }
			}
			
			List<TargetProgressEntity> result = null;
			if (recordYear != -1)
			{
				result = info.getTargetsForYear(recordYear, recordType);
			}
			else {
				result = info.getLastTargets(recordType);
			}
			
			String json = "[]";
			if (result != null)
				json = GsonService.gson.toJson(result);
			
			resp.getWriter().print(json);
		}
		else {
			path = path.substring(1);
			if (path.endsWith("/delete"))
			{
				Scanner pathScanner = new Scanner(path);
				pathScanner.useDelimiter("/");
				String recordId = pathScanner.next();
				pathScanner.close();
				
				try {
					log.info("About to delete " + Long.parseLong(recordId));
					info.removeTarget(Long.parseLong(recordId));
				}
				catch (NumberFormatException ex)
				{
					resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				}
				
			}
			else {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		}
	}
}
