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

import com.google.appengine.api.datastore.KeyFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

import net.mrcullen.targetrecording.GsonService;
import net.mrcullen.targetrecording.PupilRecordHelper;
import net.mrcullen.targetrecording.UrlPathHelper;
import net.mrcullen.targetrecording.entities.PupilEntity;
import net.mrcullen.targetrecording.entities.PupilTargetEntity;
import net.mrcullen.targetrecording.entities.TargetProgressEntity;
import net.mrcullen.targetrecording.process.PupilTargetInformation;
import net.mrcullen.targetrecording.process.TargetProgressInformation;

@SuppressWarnings("serial")
public class TargetProgressServlet extends AuthenticatedServletRequest {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(TargetProgressServlet.class.getName());
	
	public void doAuthenticatedPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		if (!UrlPathHelper.isPathEmpty(req.getPathInfo()))
		{
			log.warning("[POST] Unexpected path in URL (" + req.getPathInfo() + ")");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String targetProgress = req.getParameter("TargetProgress");
		String targetType = req.getParameter("TargetType");
		String currentLevel = req.getParameter("TargetCurrentLevel");
		String yearParam = req.getParameter("TargetYear");
		String targetId = req.getParameter("PupilTargetKey");
		
		Key<PupilTargetEntity> targetKey = UrlPathHelper.getKeyFromPath(targetId, PupilTargetEntity.class.getSimpleName());
		if (targetProgress == null || targetType == null || targetKey == null
				|| currentLevel == null || targetId == null)
		{
			log.warning("[POST] Malformed or missing parameters passed to server.");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		PupilTargetEntity pupilTarget = PupilTargetInformation.getPupilTargetEntity(targetKey);
		if (pupilTarget == null)
		{
			log.warning("[POST] Invalid key for PupilTarget provided.");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		PupilEntity pupilEntity = pupilTarget.getPupil();
		if (!Key.create(pupilEntity).equals(Key.create(req.getAttribute("UserEntity"))))
		{
			log.warning("[POST] Attempt to create entity for different user.");
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		int year = -1;
		try { 
			year = Integer.parseInt(yearParam);
		} catch (Exception ex)
		{
			log.warning("[POST] Malformed year parameter passed to server (" + yearParam + ")");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;			
		}
		
		TargetProgressEntity progress = new TargetProgressEntity (Ref.create(pupilTarget), year);
		if ((!progress.setYearGroup(year)) ||
				(!progress.setLevel(currentLevel)) ||
				(!progress.setRecordType(targetType)))
		{
			log.warning("[POST] Unable to set attributes of the new progress entity.");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;			
		}
		
		progress.setNextSteps(targetProgress);
		
		Key<TargetProgressEntity> key = TargetProgressInformation.saveTargetProgress(progress);
		
		String json = "{ }";
		if (key != null)
			json = GsonService.gson().toJson(PupilRecordHelper.constructRecord(TargetProgressInformation.getTargetProgressEntity(key)));
		else log.severe("[POST] No key returned on attempt to save progress entity to database");
		
		resp.setContentType("application/json");
		resp.getWriter().print(json);
	}

	public void doAuthenticatedPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		@SuppressWarnings("unchecked")
		Key<TargetProgressEntity> key = UrlPathHelper.getKeyFromPath(req.getPathInfo(), TargetProgressEntity.class.getSimpleName());
		if (key == null)
		{
			log.warning("[PUT] Invalid progress key passed to server.");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		TargetProgressEntity target = TargetProgressInformation.getTargetProgressEntity(key);
		if (target == null)
		{
			log.warning("[PUT] Unable to find entity for the provided progress key.");
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;			
		}

		String targetProgress = req.getParameter("TargetProgress");
		String targetType = req.getParameter("TargetType");
		String currentLevel = req.getParameter("TargetCurrentLevel");
		String yearParam = req.getParameter("TargetYear");

		if (targetProgress != null)
			target.setNextSteps(targetProgress);
		
		if (targetType != null)
		{
			if (!target.setRecordType(targetType))
			{
				log.warning("[PUT] Invalid record type passed to server (" + targetType + ")");
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;							
			}
		}
		
		if (yearParam != null)
		{
			int year = -1;
			try { 
				year = Integer.parseInt(yearParam);
			} catch (Exception ex)
			{
				log.warning("[PUT] Invalid integer passed as year to server (" + yearParam + ")");
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;			
			}
			
			if (!target.setYearGroup(year))
			{
				log.warning("[PUT] Invalid year group passed to server (" + yearParam + ")");
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;							
			}
		}
		
		if (currentLevel != null)
		{
			if (!target.setLevel(currentLevel))
			{
				log.warning("[PUT] Invalid current level passed to server (" + currentLevel + ")");
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;			
			}
		}
		
		key = TargetProgressInformation.saveTargetProgress(target);
		
		String json = "{ }";
		if (key != null)
			json = GsonService.gson().toJson(PupilRecordHelper.constructRecord(TargetProgressInformation.getTargetProgressEntity(key)));
		else log.severe("[PUT] No key returned on attempt to update progress entity in database");
		
		resp.setContentType("application/json");
		resp.getWriter().print(json);
	}

	
	public void doAuthenticatedDelete (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		@SuppressWarnings("unchecked")
		Key<TargetProgressEntity> key = UrlPathHelper.getKeyFromPath(req.getPathInfo(), TargetProgressEntity.class.getSimpleName());
		if (key == null)
		{
			log.warning("[DELETE] Malformed key passed to server.");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		TargetProgressInformation.removeTargetProgress (key);
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	public void doAuthenticatedGet (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		String json = "[ ]";

		@SuppressWarnings("unchecked")
		Key<TargetProgressEntity> key = UrlPathHelper.getKeyFromPath(req.getPathInfo(), TargetProgressEntity.class.getSimpleName());
		if (key != null)
		{
			TargetProgressEntity progress = TargetProgressInformation.getTargetProgressEntity(key);
			if (progress != null)
				json = GsonService.entityToJson(progress);
			else {
				log.warning("[GET] Key passed to server does not reference an entity.");
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}
		else {
			List<TargetProgressEntity> list = TargetProgressInformation.getAllTargetProgress();
			json = GsonService.entityToJson(list);
		}
		resp.setContentType("application/json");
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
