package net.mrcullen.targetrecording.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
public class PupilServlet extends HttpServlet {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(PupilServlet.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
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
			log.warning("no email");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String newUserName = req.getParameter("UserName");
		Key<FormEntity> newUserFormKey = UrlPathHelper.getKeyFromPath(req.getParameter("UserFormKey"), FormEntity.class.getSimpleName());
		
		if ((newUserFormKey == null) || (FormInformation.getForm(newUserFormKey) == null))
		{
			log.warning("invalid key");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;				
		}
		
		PupilEntity newPupil = new PupilEntity (newUserEmail, newUserName, Ref.create(newUserFormKey));
		Key<PupilEntity> key = PupilInformation.savePupil(newPupil);
		
		String json = "[ ]";
		if (key != null)
			json = GsonService.keyToJson(key);
		
		resp.getWriter().print(json);
	}

	public void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		Key<PupilEntity> pupilKey = UrlPathHelper.getKeyFromPath(req.getPathInfo(), PupilEntity.class.getSimpleName());
		if (pupilKey == null)
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		PupilEntity pupil = PupilInformation.getPupil(pupilKey);
		if (pupil == null)
		{
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;			
		}

		String newUserName = req.getParameter("UserName");
		if (newUserName != null)
			pupil.setName(newUserName);
		
		String newUserFormKey = req.getParameter("UserFormKey");
		if (newUserFormKey != null)
		{
			Key<FormEntity> formKey = UrlPathHelper.getKeyFromPath(newUserFormKey, FormEntity.class.getSimpleName());
			if (FormInformation.getForm(formKey) == null)
			{
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;				
			}
			pupil.setForm(Ref.create(formKey));
		}

		
		Key<PupilEntity> key = PupilInformation.savePupil(pupil);
		
		String json = "[ ]";
		if (key != null)
			json = GsonService.keyToJson(key);
		
		resp.getWriter().print(json);		
	}

	
	public void doDelete (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		Key<PupilEntity> pupilKey = UrlPathHelper.getKeyFromPath(req.getPathInfo(), PupilEntity.class.getSimpleName());
		if (pupilKey == null)
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		PupilEntity pupil = PupilInformation.getPupil(pupilKey);
		if (pupil == null)
		{
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		PupilInformation.removePupil(Key.create(pupil));
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	public void doGet (HttpServletRequest req, HttpServletResponse resp)
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
					String keyStageParam = req.getParameter("KeyStage");

					try {
						if (keyStageParam != null)
						{
							int keyStage = -1;
							if (keyStageParam.equalsIgnoreCase("current"))
								keyStage = pupil.getForm().get().getKeyStage();
							else keyStage = Integer.parseInt(keyStageParam);
							
							targetParams.put("keyStage", keyStage);
						}
						
						if (subjectParam != null)
						{
							Key<SubjectEntity> subjectId = UrlPathHelper.getKeyFromPath(subjectParam, SubjectEntity.class.getSimpleName());
							targetParams.put("subject", subjectId);
						}
					}
					catch (Exception ex) {	}
					
					List<PupilTargetEntity> list = PupilTargetInformation.findTargetInformationByPupil(pupil, targetParams);
					json = GsonService.entityToJson(list);
				}
				else if (searchType.equalsIgnoreCase("progress"))
				{
					HashMap<String, Object> progressParams = new HashMap<String, Object> ();
					HashMap<String, Object> targetParams = new HashMap<String, Object> ();

					String subjectParam = req.getParameter("SubjectId");
					String keyStageParam = req.getParameter("KeyStage");
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
						else if (keyStageParam != null)
						{
							int keyStage = -1;
							if (keyStageParam.equalsIgnoreCase("current"))
								keyStage = pupil.getForm().get().getKeyStage();
							else keyStage = Integer.parseInt(keyStageParam);
							
							targetParams.put("keyStage", keyStage);
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
					catch (Exception ex) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return;	}
					
					if (targetParams.isEmpty())
					{
						List<TargetProgressEntity> list = TargetProgressInformation.findProgressInformationByAncestor(Key.create(pupil), progressParams);
						json = GsonService.gson.toJson(PupilRecordHelper.constructRecords(list));
					}
					else {
						List<PupilTargetEntity> list = PupilTargetInformation.findTargetInformationByPupil(pupil, targetParams);
						json = GsonService.gson.toJson(PupilRecordHelper.constructRecordsFromTargets(list));
					}
				}
				else {
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
				} catch (Exception ex) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
				
				list = PupilInformation.getPupilsByForm(Key.create(FormEntity.class, formId));
			}
			else {
				list = PupilInformation.getPupils();
			}
			json = GsonService.entityToJson(list);
		}		
		resp.getWriter().print(json);
	}
}
