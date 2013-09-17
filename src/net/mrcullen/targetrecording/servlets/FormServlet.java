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
import net.mrcullen.targetrecording.entities.FormEntity;
import net.mrcullen.targetrecording.entities.SubjectEntity;
import net.mrcullen.targetrecording.process.FormInformation;

@SuppressWarnings("serial")
public class FormServlet extends HttpServlet {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(FormServlet.class.getName());


	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		if (!UrlPathHelper.isPathEmpty(req.getPathInfo()))
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String formCode = req.getParameter("FormCode");
		String formIntakeYear = req.getParameter("FormIntakeYear");
		
		int intakeYear = -1;
		try {
			intakeYear = Integer.parseInt(formIntakeYear);
		} catch (Exception ex) { resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); return; }
		
		FormEntity form = new FormEntity (formCode, intakeYear);
		Key<FormEntity> key = FormInformation.saveForm(form);
		
		String json = "[ ]";
		if (key != null)
			json = GsonService.keyToJson(key);
		
		resp.getWriter().print(json);
	}

	public void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		Key<FormEntity> formKey = UrlPathHelper.getKeyFromPath(req.getPathInfo(), FormEntity.class.getSimpleName());
		if (formKey == null)
		{
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String formCode = req.getParameter("FormCode");
		String formIntakeYear = req.getParameter("FormIntakeYear");
		int intakeYear = -1;
		
		if (formIntakeYear != null)
		{
			try {
				intakeYear = Integer.parseInt(formIntakeYear);
			} catch (Exception ex) { resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); return; }
		}

		FormEntity form = FormInformation.getForm(formKey);
		if (form == null)
		{
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;			
		}
		
		if ((formCode != null) && (!formCode.isEmpty()))
			form.setFormCode(formCode);
		
		if (intakeYear != -1)
			form.setIntakeYear(intakeYear);
		
		Key<FormEntity> key = FormInformation.saveForm(form);
		
		String json = "[ ]";
		if (key != null)
			json = GsonService.keyToJson(key);
		
		resp.getWriter().print(json);
	}

	
	public void doDelete (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		Key<FormEntity> formKey = UrlPathHelper.getKeyFromPath(req.getPathInfo(), FormEntity.class.getSimpleName());
		if (formKey == null)
		{
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;			
		}
		
		FormInformation.removeForm(formKey);
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	public void doGet (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		String json = "[ ]";
		
		Key<FormEntity> formKey = UrlPathHelper.getKeyFromPath(req.getPathInfo(), FormEntity.class.getSimpleName());
		if (formKey == null)
		{
			String formCode = req.getParameter("FormCode");
			if (formCode != null)
			{
				FormEntity form = FormInformation.getFormByStaffCode(formCode);
				if (form == null)
				{
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
					return;						
				}
				json = GsonService.entityToJson(form);				
			}
			else {
				List<FormEntity> list = null;
				String intakeYearParam = req.getParameter("FormIntakeYear");
				if (intakeYearParam != null)
				{
					int intakeYear = -1;
					try {
						intakeYear = Integer.parseInt(intakeYearParam);
					} catch (Exception ex) { resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); return; }
					
					list = FormInformation.getFormByIntakeYear(intakeYear);
				}
				else {
					list = FormInformation.getForms();
				}
				json = GsonService.entityToJson(list);
			}
		}
		else {
			FormEntity form = FormInformation.getForm(formKey);
			if (form == null)
			{
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;						
			}
			json = GsonService.entityToJson(form);
		}
		
		resp.getWriter().print(json);
	}	
}
