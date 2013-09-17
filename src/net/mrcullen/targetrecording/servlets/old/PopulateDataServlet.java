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
public class PopulateDataServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(PopulateDataServlet.class.getName());
	

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		PopulateData.populateForms();
		PopulateData.populateSubjects();
		PopulateData.populatePupil();
	}
}
