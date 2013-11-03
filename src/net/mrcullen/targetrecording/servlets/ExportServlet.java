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

import com.csvreader.CsvWriter;
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
public class ExportServlet extends AuthenticatedServletRequest {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ExportServlet.class.getName());
	
	public void doAuthenticatedGet (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		if (!UrlPathHelper.isPathEmpty(req.getPathInfo()))
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String formParam = req.getParameter("Form");
		String typeParam = req.getParameter("Type");
		String yearParam = req.getParameter("Year");		
		
		int year = -1;
		try {
			if (yearParam != null)
				year = Integer.parseInt(yearParam);
		}
		catch (Exception ex)
		{
			log.warning("[GET] Invalid year parameter passed (" + yearParam + ")");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;			
		}
		
		Key<FormEntity> formId = UrlPathHelper.getKeyFromPath(formParam, FormEntity.class.getSimpleName());
		HashMap<String, Object> parameter = new HashMap<String, Object>();
		
		if (typeParam == null)
		{
			log.warning("[GET] Invalid form parameter passed (" + formParam + ")");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		parameter.put("recordType", typeParam);
		
		resp.setContentType("text/csv");
		resp.setHeader("Content-Disposition", "attachment; filename=progressexport.csv");
		
		CsvWriter writer = new CsvWriter (resp.getWriter(), ',');
		writer.write("Pupil Email");
		writer.write("Pupil Name");
		writer.write("Year Group");
		writer.write("Subject");
		writer.write("3 Levels");
		writer.write("4 Levels");
		writer.write("5 Levels");
		writer.write("Current Level");
		writer.write("Target");
		writer.endRecord();
		
		List<PupilEntity> pupils = null;
		
		if (year != -1)
		{
			pupils = PupilInformation.getPupilsByIntakeYear(year);
		}
		else
		{
			if (formId != null)
			{
				pupils = PupilInformation.getPupilsByForm(formId);
			}
			else
			{
				pupils = PupilInformation.getPupils();
			}
		}
		
		for (PupilEntity pupil : pupils)
		{
			int currentYearGroup = pupil.getForm().get().getYearGroup();
			List<PupilTargetEntity> targets = PupilTargetInformation.findCurrentTargetInformationByPupil(pupil, null);
			for (PupilTargetEntity target : targets)
			{
				HashMap<String, Object> finalParmeters = (HashMap<String, Object>)parameter.clone();
				finalParmeters.put("yearGroup", currentYearGroup);
				
				List<TargetProgressEntity> progresses = TargetProgressInformation.findProgressInformationByAncestor
						(Key.create(target), finalParmeters);
				
				for (TargetProgressEntity progress : progresses)
				{
					writer.write(pupil.getEmail());
					writer.write(pupil.getName());
					writer.write(Integer.toString(progress.getYearGroup()));
					writer.write(target.getSubject().getName());
					writer.write(target.getThreeLevelsTargetGrade());
					writer.write(target.getFourLevelsTargetGrade());
					writer.write(target.getFiveLevelsTargetGrade());
					writer.write(progress.getCurrentLevel());
					writer.write(progress.getNextSteps());
					writer.endRecord();
				}
			}
		}
		writer.close();
	}
	
	@Override
	public Set<String> getRequiredPermission(String method) {
		TreeSet<String> permissions = new TreeSet<String>();
		permissions.add(ADMIN_PERMISSION);
		permissions.add(TEACHER_PERMISSION);
			
		return permissions;
	}
}
