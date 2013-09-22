package net.mrcullen.targetrecording.servlets;

import java.io.IOException;
import java.util.List;
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
import net.mrcullen.targetrecording.entities.PupilTargetEntity;
import net.mrcullen.targetrecording.entities.TargetProgressEntity;
import net.mrcullen.targetrecording.process.PupilTargetInformation;
import net.mrcullen.targetrecording.process.TargetProgressInformation;

@SuppressWarnings("serial")
public class TargetProgressServlet extends HttpServlet {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(TargetProgressServlet.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		if (!UrlPathHelper.isPathEmpty(req.getPathInfo()))
		{
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
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		PupilTargetEntity pupilTarget = PupilTargetInformation.getPupilTargetEntity(targetKey);
		if (pupilTarget == null)
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		int year = -1;
		try { 
			year = Integer.parseInt(yearParam);
		} catch (Exception ex)
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;			
		}
		
		TargetProgressEntity progress = new TargetProgressEntity (Ref.create(pupilTarget), year);
		if ((!progress.setYearGroup(year)) ||
				(!progress.setLevel(currentLevel)) ||
				(!progress.setRecordType(targetType)))
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;			
		}
		
		progress.setNextSteps(targetProgress);
		
		Key<TargetProgressEntity> key = TargetProgressInformation.saveTargetProgress(progress);
		
		String json = "{ }";
		if (key != null)
			json = GsonService.gson.toJson(PupilRecordHelper.constructRecord(TargetProgressInformation.getTargetProgressEntity(key)));
		
		resp.getWriter().print(json);
	}

	public void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		@SuppressWarnings("unchecked")
		Key<TargetProgressEntity> key = UrlPathHelper.getKeyFromPath(req.getPathInfo(), TargetProgressEntity.class.getSimpleName());
		if (key == null)
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		TargetProgressEntity target = TargetProgressInformation.getTargetProgressEntity(key);
		if (target == null)
		{
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
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;			
			}
			
			if (!target.setYearGroup(year))
			{
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;							
			}
		}
		
		if (currentLevel != null)
		{
			if (!target.setLevel(currentLevel))
			{
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;			
			}
		}
		
		key = TargetProgressInformation.saveTargetProgress(target);
		
		String json = "{ }";
		if (key != null)
			json = GsonService.gson.toJson(PupilRecordHelper.constructRecord(TargetProgressInformation.getTargetProgressEntity(key)));
		
		resp.getWriter().print(json);
	}

	
	public void doDelete (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		@SuppressWarnings("unchecked")
		Key<TargetProgressEntity> key = UrlPathHelper.getKeyFromPath(req.getPathInfo(), TargetProgressEntity.class.getSimpleName());
		if (key == null)
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		TargetProgressInformation.removeTargetProgress (key);
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	public void doGet (HttpServletRequest req, HttpServletResponse resp)
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
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}
		else {
			List<TargetProgressEntity> list = TargetProgressInformation.getAllTargetProgress();
			json = GsonService.entityToJson(list);
		}		
		resp.getWriter().print(json);
	}
}
