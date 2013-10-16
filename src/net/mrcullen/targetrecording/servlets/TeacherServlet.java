package net.mrcullen.targetrecording.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;

import net.mrcullen.targetrecording.GsonService;
import net.mrcullen.targetrecording.UrlPathHelper;
import net.mrcullen.targetrecording.entities.FormEntity;
import net.mrcullen.targetrecording.entities.TeacherEntity;
import net.mrcullen.targetrecording.process.TeacherInformation;

@SuppressWarnings("serial")
public class TeacherServlet extends AuthenticatedServletRequest {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(TeacherServlet.class.getName());
	
	public void doAuthenticatedPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		if (!UrlPathHelper.isPathEmpty(req.getPathInfo()))
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String newUserEmail = req.getParameter("UserEmail");
		if (newUserEmail == null)
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String newUserName = req.getParameter("UserName");
		String newUserStaffCode = req.getParameter("UserStaffCode");
		boolean newUserAdmin = Boolean.valueOf(req.getParameter("UserAdmin"));
		
		TeacherEntity newTeacher = new TeacherEntity (newUserEmail, newUserName, newUserStaffCode, newUserAdmin);
		Key<TeacherEntity> key = TeacherInformation.saveTeacher(newTeacher);
		
		String json = "[ ]";
		if (key != null)
			json = GsonService.keyToJson(key);
		
		resp.setContentType("application/json");
		resp.getWriter().print(json);
	}

	public void doAuthenticatedPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		Key<TeacherEntity> teacherKey = UrlPathHelper.getKeyFromPath(req.getPathInfo(), TeacherEntity.class.getSimpleName());
		if (teacherKey == null)
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		TeacherEntity teacher = TeacherInformation.getTeacher(teacherKey);
		if (teacher == null)
		{
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;			
		}

		String newUserName = req.getParameter("UserName");
		if (newUserName != null)
			teacher.setName(newUserName);
		
		String newUserStaffCode = req.getParameter("UserStaffCode");
		if (newUserStaffCode != null)
			teacher.setStaffCode(newUserStaffCode);
		
		String newUserAdmin = req.getParameter("UserAdmin");
		if (newUserAdmin != null)
			teacher.setAdminPermission (Boolean.valueOf(newUserAdmin));
		
		Key<TeacherEntity> key = TeacherInformation.saveTeacher(teacher);
		
		String json = "[ ]";
		if (key != null)
			json = GsonService.keyToJson(key);
		
		resp.setContentType("application/json");
		resp.getWriter().print(json);		
	}

	
	public void doAuthenticatedDelete (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		Key<TeacherEntity> teacherKey = UrlPathHelper.getKeyFromPath(req.getPathInfo(), TeacherEntity.class.getSimpleName());
		if (teacherKey == null)
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		TeacherEntity teacher = TeacherInformation.getTeacher(teacherKey);
		if (teacher == null)
		{
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		TeacherInformation.removeTeacher(Key.create(teacher));
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	public void doAuthenticatedGet (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		String json = "[ ]";

		Key<TeacherEntity> teacherKey = UrlPathHelper.getKeyFromPath(req.getPathInfo(), TeacherEntity.class.getSimpleName());
		if (teacherKey != null)
		{
			TeacherEntity teacher = TeacherInformation.getTeacher(teacherKey);
			if (teacher != null)
				json = GsonService.entityToJson(teacher);
			else {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}
		else {
			String staffCode = req.getParameter("UserStaffCode");
			if (staffCode != null)
			{
				TeacherEntity teacher = TeacherInformation.getTeacherByStaffCode(staffCode);
				if (teacher != null)
					json = GsonService.entityToJson(teacher);
				else {
					resp.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
			}
			else {
				List<TeacherEntity> list = TeacherInformation.getTeachers();
				json = GsonService.entityToJson(list);
			}
		}
		resp.setContentType("application/json");
		resp.getWriter().print(json);
	}

	@Override
	public Set<String> getRequiredPermission(String method) {
		TreeSet<String> permissions = new TreeSet<String>();
		permissions.add(ADMIN_PERMISSION);
		if (method.equals("GET"))
		{
			permissions.add(OWN_PERMISSION);
			permissions.add(TEACHER_PERMISSION);
		}
		else if (method.equals("POST"))
		{
			permissions.add(PUPIL_PERMISSION);
		}
		else if (method.equals("PUT"))
		{
			permissions.add(OWN_PERMISSION);			
		}
			
		return permissions;
	}
}
