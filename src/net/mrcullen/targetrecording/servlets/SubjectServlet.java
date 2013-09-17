package net.mrcullen.targetrecording.servlets;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;

import net.mrcullen.targetrecording.GsonService;
import net.mrcullen.targetrecording.UrlPathHelper;
import net.mrcullen.targetrecording.entities.SubjectEntity;
import net.mrcullen.targetrecording.process.SubjectInformation;

@SuppressWarnings("serial")
public class SubjectServlet extends HttpServlet {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SubjectServlet.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		if (!UrlPathHelper.isPathEmpty(req.getPathInfo()))
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String subjectName = req.getParameter("SubjectName");
		String subjectVocational = req.getParameter("SubjectVocational");

		if ((subjectName == null) || (subjectVocational == null))
		{
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		boolean vocational = Boolean.valueOf(subjectVocational);
		SubjectEntity subject = new SubjectEntity (subjectName, vocational);
		Key<SubjectEntity> key = SubjectInformation.saveSubject(subject);
		
		String json = "[ ]";
		if (key != null)
			json = GsonService.keyToJson(key);
		
		resp.getWriter().print(json);
	}

	public void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		Key<SubjectEntity> subjectKey = UrlPathHelper.getKeyFromPath(req.getPathInfo(), SubjectEntity.class.getSimpleName());
		if (subjectKey == null)
		{
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;			
		}

		String subjectName = req.getParameter("SubjectName");
		String subjectVocational = req.getParameter("SubjectVocational");
				
		SubjectEntity subject = SubjectInformation.getSubject(subjectKey);
		if (subject == null)
		{
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;			
		}
		
		if ((subjectName != null) && (!subjectName.isEmpty()))
			subject.setName(subjectName);
		
		if ((subjectVocational != null) && (!subjectVocational.isEmpty()))
			subject.setVocational(Boolean.valueOf(subjectVocational));
		
		Key<SubjectEntity> key = SubjectInformation.saveSubject(subject);
		
		String json = "[ ]";
		if (key != null)
			json = GsonService.keyToJson(key);
		
		resp.getWriter().print(json);
	}

	
	public void doDelete (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		Key<SubjectEntity> subjectKey = UrlPathHelper.getKeyFromPath(req.getPathInfo(), SubjectEntity.class.getSimpleName());
		if (subjectKey == null)
		{
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;			
		}
		
		SubjectInformation.removeSubject(subjectKey);
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	public void doGet (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		String json = "[ ]";
		
		Key<SubjectEntity> subjectKey = UrlPathHelper.getKeyFromPath(req.getPathInfo(), SubjectEntity.class.getSimpleName());
		if (subjectKey == null)
		{
			List<SubjectEntity> list = SubjectInformation.getSubjects();
			json = GsonService.entityToJson(list);
		}
		else {
			SubjectEntity subject = SubjectInformation.getSubject(subjectKey);
			if (subject == null)
			{
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;						
			}
			json = GsonService.entityToJson(subject);
		}
		
		resp.getWriter().print(json);
	}	
}
