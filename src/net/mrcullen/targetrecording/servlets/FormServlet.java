package net.mrcullen.targetrecording.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;


import net.mrcullen.targetrecording.GsonService;
import net.mrcullen.targetrecording.UrlPathHelper;
import net.mrcullen.targetrecording.entities.FormEntity;

import net.mrcullen.targetrecording.process.FormInformation;

@SuppressWarnings("serial")
public class FormServlet extends AuthenticatedServletRequest {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(FormServlet.class.getName());


	public void doAuthenticatedPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		if (!UrlPathHelper.isPathEmpty(req.getPathInfo()))
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String json = "[ ]";
		if (req.getParameter("BulkFormCSVData") != null)
		{
			// Its a bulk request
			ArrayList<FormEntity> list = new ArrayList<FormEntity> ();
			StringTokenizer tokens = new StringTokenizer (req.getParameter("BulkFormCSVData"),"[,\n\r]+");
			
			try {
				while (tokens.hasMoreTokens()) {
					Key<FormEntity> key = createFormEntity(tokens.nextToken(), tokens.nextToken());
					if (key == null)
						throw new Exception ();
					
					list.add(FormInformation.getForm(key));
				}
			} catch (Exception e) {
				Iterator<FormEntity> items = list.iterator();
				while (items.hasNext())
				{
					Key<FormEntity> key = Key.create(items.next());
					FormInformation.removeForm(key);
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}
			}
			
			json = GsonService.entityToJson(list);
			
		}
		else {
			json = "{ }";
			String formCode = req.getParameter("FormCode");
			String formIntakeYear = req.getParameter("FormIntakeYear");
			
			Key<FormEntity> key = createFormEntity (formCode, formIntakeYear);
			if (key == null)
			{
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			
			json = GsonService.entityToJson(FormInformation.getForm(key));
		}
		resp.setContentType("application/json");
		resp.getWriter().print(json);
	}
	
	protected Key<FormEntity> createFormEntity (String formCode, String formIntakeYear)
	{
		if (formCode == null)
			return null;
		
		int intakeYear = -1;
		try {
			intakeYear = Integer.parseInt(formIntakeYear);
		} catch (Exception ex) { return null; }
		
		FormEntity form = new FormEntity (formCode, intakeYear);
		return FormInformation.saveForm(form);
	}

	public void doAuthenticatedPut(HttpServletRequest req, HttpServletResponse resp)
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
			json = GsonService.entityToJson(FormInformation.getForm(key));
		
		resp.setContentType("application/json");
		resp.getWriter().print(json);
	}

	
	public void doAuthenticatedDelete (HttpServletRequest req, HttpServletResponse resp)
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
	
	public void doAuthenticatedGet (HttpServletRequest req, HttpServletResponse resp)
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
		
		resp.setContentType("application/json");
		resp.getWriter().print(json);
	}

	@Override
	public Set<String> getRequiredPermission(String method) {
		TreeSet<String> permissions = new TreeSet<String>();
		if (method.equals("GET"))
		{
			permissions.add(ALL_PERMISSION);
		}
		else {
			permissions.add(ADMIN_PERMISSION);
		}
		return permissions;
	}	
}
