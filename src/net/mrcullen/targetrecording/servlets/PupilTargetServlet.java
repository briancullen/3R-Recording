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

import java.util.HashMap;
import com.googlecode.objectify.Key;

import net.mrcullen.targetrecording.GsonService;
import net.mrcullen.targetrecording.UrlPathHelper;
import net.mrcullen.targetrecording.entities.PupilEntity;
import net.mrcullen.targetrecording.entities.PupilTargetEntity;
import net.mrcullen.targetrecording.entities.SubjectEntity;
import net.mrcullen.targetrecording.process.PupilInformation;
import net.mrcullen.targetrecording.process.PupilTargetInformation;
import net.mrcullen.targetrecording.process.SubjectInformation;

@SuppressWarnings("serial")
public class PupilTargetServlet extends AuthenticatedServletRequest {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(PupilTargetServlet.class.getName());
	
	public void doAuthenticatedPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		if (!UrlPathHelper.isPathEmpty(req.getPathInfo()))
		{
			log.warning("[POST] Unexpected path in URL (" + req.getPathInfo() + ")");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String targetPupilKey = req.getParameter("TargetPupilKey");
		String targetSubjectKey = req.getParameter("TargetSubjectKey");
		String targetKeyStage = req.getParameter("TargetKeyStage");
		String threeLevels = req.getParameter("ThreeLevelsTarget");
		String fourLevels = req.getParameter("FourLevelsTarget");
		String fiveLevels = req.getParameter("FiveLevelsTarget");

		Key<SubjectEntity> subjectKey = UrlPathHelper.getKeyFromPath(targetSubjectKey, SubjectEntity.class.getSimpleName());
		Key<PupilEntity> pupilKey = UrlPathHelper.getKeyFromPath(targetPupilKey, PupilEntity.class.getSimpleName());
		
		if (threeLevels == null || fourLevels == null
				|| fiveLevels == null || pupilKey == null || subjectKey == null)
		{
			log.warning("[POST] Malformed or missing parameters passed to server.");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		if (!pupilKey.equals(Key.create(req.getAttribute("UserEntity"))))
		{
			log.warning("[POST] Attempt to create entity for different user.");
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		int keyStage = -1;
		try {
			keyStage = Integer.parseInt(targetKeyStage);			
		}
		catch (NumberFormatException ex) {
			log.warning("[POST] Malformed key stage parameter passed to server (" + keyStage + ")");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		PupilEntity pupil = PupilInformation.getPupil(pupilKey);
		SubjectEntity subject = SubjectInformation.getSubject(subjectKey);
		if ((keyStage < 3 || keyStage > 5)
				|| (pupil == null) || (subject == null))
		{
			log.warning("[POST] Invalid key stage parameter passed to server (" + keyStage + ")");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;				
		}

		HashMap parameters = new HashMap();
		parameters.put("keyStage", keyStage);
		parameters.put("subject", subjectKey);
		if (!PupilTargetInformation.findTargetInformationByPupil(pupil, parameters).isEmpty())
		{
			log.warning("[POST] Target already exists for the specified tuple (" + keyStage + ", " + subject.getName() + ")");
			resp.sendError(HttpServletResponse.SC_CONFLICT);
			return;										
		}
		

		PupilTargetEntity newTarget = new PupilTargetEntity(pupil, subject, keyStage);
		if (!newTarget.setTargetGrades(threeLevels, fourLevels, fiveLevels))
		{
			log.warning("[POST] Invalid target grades passed to the server (" + threeLevels + "," + fourLevels + "," + fiveLevels + ")");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;							
		}
		
		Key<PupilTargetEntity> key = PupilTargetInformation.saveTarget(newTarget);
		newTarget = PupilTargetInformation.getPupilTargetEntity(key);
		
		String json = "{ }";
		if (key != null)
			json = GsonService.entityToJson(newTarget);
		else log.severe("[POST] No key returned on attempt to save target entity to database");
		
		resp.getWriter().print(json);
	}

	public void doAuthenticatedPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		@SuppressWarnings("unchecked")
		Key<PupilTargetEntity> key = UrlPathHelper.getKeyFromPath(req.getPathInfo(), PupilTargetEntity.class.getSimpleName());
		if (key == null)
		{
			log.warning("[PUT] Invalid target key passed to server.");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		PupilTargetEntity target = PupilTargetInformation.getPupilTargetEntity(key);
		if (target == null)
		{
			log.warning("[PUT] Unable to find entity for the provided target key.");
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;			
		}
		
		String targetKeyStage = req.getParameter("TargetKeyStage");
		String threeLevels = req.getParameter("ThreeLevelsTarget");
		String fourLevels = req.getParameter("FourLevelsTarget");
		String fiveLevels = req.getParameter("FiveLevelsTarget");

		if (targetKeyStage != null)
		{
			int keyStage = -1;
			try {
				keyStage = Integer.parseInt(targetKeyStage);
			} catch (Exception ex) {
				log.warning("[PUT] Malformed key stage parameter passed to server (" + targetKeyStage + ")");
			}
			
			if (!target.setKeyStage(keyStage))
			{
				log.warning("[PUT] Invalid key stage parameter passed to server (" + keyStage + ")");
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;								
			}
		}

		if ((threeLevels != null) && (fourLevels != null) && (fiveLevels != null))
		{
			if (!target.setTargetGrades(threeLevels, fourLevels, fiveLevels))
			{
				log.warning("[POST] Invalid target grades passed to the server (" + threeLevels + "," + fourLevels + "," + fiveLevels + ")");
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;				
			}
		}
		
		key = PupilTargetInformation.saveTarget(target);
		
		String json = "{ }";
		if (key != null)
			json = GsonService.entityToJson(target);
		else log.severe("[PUT] No key returned on attempt to save target entity to database");
		
		resp.getWriter().print(json);		
	}

	
	public void doAuthenticatedDelete (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		@SuppressWarnings("unchecked")
		Key<PupilTargetEntity> key = UrlPathHelper.getKeyFromPath(req.getPathInfo(), PupilTargetEntity.class.getSimpleName());
		if (key == null)
		{
			log.warning("[DELETE] Malformed key passed to server.");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		PupilTargetInformation.removeTarget (key);
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	public void doAuthenticatedGet (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		String json = "[ ]";

		@SuppressWarnings("unchecked")
		Key<PupilTargetEntity> key = UrlPathHelper.getKeyFromPath(req.getPathInfo(), PupilTargetEntity.class.getSimpleName());
		if (key != null)
		{
			PupilTargetEntity pupilTarget = PupilTargetInformation.getPupilTargetEntity(key);
			if (pupilTarget != null)
				json = GsonService.entityToJson(pupilTarget);
			else {
				log.warning("[GET] No entity found to match the specified key.");
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}
		else {
			List<PupilTargetEntity> list = PupilTargetInformation.getAllPupilTargets();
			json = GsonService.entityToJson(list);
		}		
		resp.getWriter().print(json);
	}
	
	@Override
	public Set<String> getRequiredPermission(String method) {
		TreeSet<String> permissions = new TreeSet<String>();
		permissions.add(ADMIN_PERMISSION);
		
		if (method.equals("POST"))
			permissions.add(PUPIL_PERMISSION);
		else permissions.add(OWN_PERMISSION);
		
		if (method.equals("GET"))
		{
			permissions.add(TEACHER_PERMISSION);
		}
			
		return permissions;
	}
}
