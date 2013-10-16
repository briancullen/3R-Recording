package net.mrcullen.targetrecording.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

import net.mrcullen.targetrecording.GradeHelper;
import net.mrcullen.targetrecording.GsonService;
import net.mrcullen.targetrecording.PupilRecordHelper;
import net.mrcullen.targetrecording.UrlPathHelper;
import net.mrcullen.targetrecording.entities.FormEntity;
import net.mrcullen.targetrecording.entities.PupilEntity;
import net.mrcullen.targetrecording.entities.PupilTargetEntity;
import net.mrcullen.targetrecording.entities.SubjectEntity;
import net.mrcullen.targetrecording.entities.TargetProgressEntity;
import net.mrcullen.targetrecording.entities.TeacherEntity;
import net.mrcullen.targetrecording.process.FormInformation;
import net.mrcullen.targetrecording.process.PupilInformation;
import net.mrcullen.targetrecording.process.PupilTargetInformation;
import net.mrcullen.targetrecording.process.TargetProgressInformation;

@SuppressWarnings("serial")
public class PupilServlet extends AuthenticatedServletRequest {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(PupilServlet.class.getName());
	
	public void doAuthenticatedPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		if (!UrlPathHelper.isPathEmpty(req.getPathInfo()))
		{
			log.warning("[POST] Unexpected path in URL (" + req.getPathInfo() + ")");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String newUserEmail = req.getParameter("UserEmail");
		if (newUserEmail == null)
		{
			log.warning("[POST] User email not specified as part of request.");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String newUserName = req.getParameter("UserName");
		if ((newUserName == null) || (newUserName.isEmpty()))
			newUserName = newUserEmail;
		
		Key<FormEntity> newUserFormKey = UrlPathHelper.getKeyFromPath(req.getParameter("UserFormKey"), FormEntity.class.getSimpleName());
		
		if ((newUserFormKey == null) || (FormInformation.getForm(newUserFormKey) == null))
		{
			log.warning("[POST] Invalid or malformed form key passed to server.");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;				
		}
		
		PupilEntity newPupil = new PupilEntity (newUserEmail, newUserName, Ref.create(newUserFormKey));
		Key<PupilEntity> key = PupilInformation.savePupil(newPupil);
		
		String json = "[ ]";
		if (key != null)
			json = GsonService.entityToJson(PupilInformation.getPupil(key));
		else log.severe("[POST] No key returned on attempt to save target entity to database");
		
		resp.setContentType("application/json");
		resp.getWriter().print(json);
	}

	public void doAuthenticatedPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		Key<PupilEntity> pupilKey = UrlPathHelper.getKeyFromPath(req.getPathInfo(), PupilEntity.class.getSimpleName());
		if (pupilKey == null)
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			log.warning("[PUT] Invalid or malformed pupil key passed to server.");
			return;
		}

		PupilEntity pupil = PupilInformation.getPupil(pupilKey);
		if (pupil == null)
		{
			log.warning("[PUT] No pupil entity found to match the provided key.");
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;			
		}

		String newUserName = req.getParameter("UserName");
		if (newUserName != null)
		{
			if (newUserName.length() == 0)
				pupil.setName(pupil.getEmail());
			else pupil.setName(newUserName);
		}
		
		String newUserFormKey = req.getParameter("UserFormKey");
		if (newUserFormKey != null)
		{
			Key<FormEntity> formKey = UrlPathHelper.getKeyFromPath(newUserFormKey, FormEntity.class.getSimpleName());
			if (FormInformation.getForm(formKey) == null)
			{
				log.warning("[PUT] Invalid or malformed form key passed to server.");
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;				
			}
			pupil.setForm(Ref.create(formKey));
		}

				Key<PupilEntity> key = PupilInformation.savePupil(pupil);
		
		String json = "[ ]";
		if (key != null)
			json = GsonService.entityToJson(PupilInformation.getPupil(key));
		else log.severe("[PUT] No key returned on attempt to save target entity to database");
		
		resp.setContentType("application/json");
		resp.getWriter().print(json);		
	}

	
	public void doAuthenticatedDelete (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		Key<PupilEntity> pupilKey = UrlPathHelper.getKeyFromPath(req.getPathInfo(), PupilEntity.class.getSimpleName());
		if (pupilKey == null)
		{
			log.warning("[DELETE] Malformed key passed to server.");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		PupilEntity pupil = PupilInformation.getPupil(pupilKey);
		if (pupil == null)
		{
			log.warning("[DELETE] No entity found to match the provided key.");
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		PupilInformation.removePupil(Key.create(pupil));
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	public void doAuthenticatedGet (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		String json = "[ ]";

		UrlPathHelper pathInfo = new UrlPathHelper (req.getPathInfo());
		Key<PupilEntity> pupilKey = pathInfo.getKeyFromPath(PupilEntity.class.getSimpleName());
		if (pupilKey != null)
		{
			PupilEntity pupil = PupilInformation.getPupil(pupilKey);
			if (pupil != null)
				json = GsonService.entityToJson(pupil);
			else {
				log.warning("[GET] No entity found to match the specified key.");
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			
			
			if (!pathInfo.isPathEmpty())
			{
				String searchType = pathInfo.getNameFromPath();
				if (searchType.equalsIgnoreCase("target"))
				{
					HashMap<String,Object> targetParams = new HashMap<String,Object> ();
					String subjectParam = req.getParameter("SubjectId");
					String stageParam = req.getParameter("Stage");

					try {
						if (stageParam != null)
						{
							int stage = -1;
							if (stageParam.equalsIgnoreCase("current"))
								stage = pupil.getForm().get().getStage();
							else stage = Integer.parseInt(stageParam);
							
							targetParams.put("targetStage", stage);
						}
						
						if (subjectParam != null)
						{
							Key<SubjectEntity> subjectId = UrlPathHelper.getKeyFromPath(subjectParam, SubjectEntity.class.getSimpleName());
							targetParams.put("subject", subjectId);
						}
					}
					catch (Exception ex) {
						log.severe("[GET] Exception thrown while process pupil target search: " + ex.toString());
						resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
						return;
					}
					
					List<PupilTargetEntity> list = PupilTargetInformation.findTargetInformationByPupil(pupil, targetParams);
					json = GsonService.entityToJson(list);
				}
				else if (searchType.equalsIgnoreCase("progress"))
				{
					HashMap<String, Object> progressParams = new HashMap<String, Object> ();
					HashMap<String, Object> targetParams = new HashMap<String, Object> ();

					String subjectParam = req.getParameter("SubjectId");
					String stageParam = req.getParameter("Stage");
					String yearParam = req.getParameter("Year");
					String recordType = req.getParameter("Type");
					
					try {
						if (yearParam != null)
						{
							int year = -1;
							if (yearParam.equalsIgnoreCase("current"))
								year = pupil.getForm().get().getYearGroup();
							else year = Integer.parseInt(yearParam);
							
							progressParams.put("yearGroup", year);
						}
						else if (stageParam != null)
						{
							int stage = -1;
							if (stageParam.equalsIgnoreCase("current"))
								stage = pupil.getForm().get().getStage();
							else stage = Integer.parseInt(stageParam);
							
							targetParams.put("targetStage", stage);
						}
						
						if (subjectParam != null)
						{
							Key<SubjectEntity> subjectId = UrlPathHelper.getKeyFromPath(subjectParam, SubjectEntity.class.getSimpleName());
							targetParams.put("subject", subjectId);
						}
						
						if (recordType != null)
						{
							progressParams.put("recordType", recordType);
						}
					}
					catch (Exception ex) {
						log.severe("[GET] Exception thrown while process pupil progress search: " + ex.toString());
						resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
						return;
					}
					
					if (targetParams.isEmpty())
					{
						List<TargetProgressEntity> list = TargetProgressInformation.findProgressInformationByAncestor(Key.create(pupil), progressParams);
						json = GsonService.gson().toJson(PupilRecordHelper.constructRecords(list));
					}
					else {
						List<PupilTargetEntity> list = PupilTargetInformation.findTargetInformationByPupil(pupil, targetParams);
						json = GsonService.gson().toJson(PupilRecordHelper.constructRecordsFromTargets(list));
					}
				}
				else {
					log.warning("[GET] Unrecognised search type requested (" + searchType + ")");	
					resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}
			}
		}
		else {
			List<PupilEntity> list = null;
			String formIdParam = req.getParameter("UserFormId");
			if (formIdParam != null)
			{
				long formId = -1;
				try {
					formId = Long.parseLong(formIdParam);
				} catch (Exception ex) {
					log.warning("[GET] Unable to parse formID (" + formIdParam + ")");
					resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}
				
				list = PupilInformation.getPupilsByForm(Key.create(FormEntity.class, formId));
			}
			else {
				list = PupilInformation.getPupils();
			}
			json = GsonService.entityToJson(list);
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
