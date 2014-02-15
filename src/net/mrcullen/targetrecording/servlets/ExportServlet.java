package net.mrcullen.targetrecording.servlets;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.csvreader.CsvWriter;
import com.google.appengine.api.ThreadManager;
import com.googlecode.objectify.Key;

import net.mrcullen.targetrecording.UrlPathHelper;
import net.mrcullen.targetrecording.entities.FormEntity;
import net.mrcullen.targetrecording.entities.PupilEntity;
import net.mrcullen.targetrecording.entities.PupilTargetEntity;
import net.mrcullen.targetrecording.entities.TargetProgressEntity;

import net.mrcullen.targetrecording.process.PupilInformation;
import net.mrcullen.targetrecording.process.PupilTargetInformation;
import net.mrcullen.targetrecording.process.TargetProgressInformation;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
		
		if (typeParam == null)
		{
			log.warning("[GET] Invalid form parameter passed (" + formParam + ")");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		
		// resp.setContentType("text/csv");
		// resp.setHeader("Content-Disposition", "attachment; filename=progressexport.csv");
		resp.getWriter().write("Processing your request. Please check your email in a few minutes.");
		
		Thread thread = ThreadManager.createBackgroundThread(new ExportClass(year, formId, typeParam));
		thread.start();
	}
	
	private static class ExportClass implements Runnable {
		
		protected int year;
		protected Key<FormEntity> formId;
		protected String typeParam;
		
		public ExportClass (int year, Key<FormEntity> formId, String typeParam)
		{
			this.year = year;
			this.formId = formId;
			this.typeParam = typeParam;
		}
		
		public void run ()
		{
			try {
				HashMap<String, Object> parameter = new HashMap<String, Object>();
				parameter.put("recordType", typeParam);
				StringWriter data = new StringWriter();
				
				CsvWriter writer = new CsvWriter (data, ',');
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
				
				log.info("About to search for students");
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
				
				log.info("Processing " + pupils.size() + " pupils");
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
				
				log.info("Creating the mail message");
				Session session = Session.getDefaultInstance(new Properties(), null);
	            Message msg = new MimeMessage(session);
	            msg.setFrom(new InternetAddress("bcullen@rossettlearning.co.uk", "3Rs Admin"));
	            msg.addRecipient(Message.RecipientType.TO,
	                             new InternetAddress("bcullen@rossettschool.co.uk", "Mr. Cullen"));
	            msg.setSubject("Export of 3Rs Data");
	            
	            Multipart mp = new MimeMultipart();
	
	            MimeBodyPart textPart = new MimeBodyPart();
	            textPart.setContent("Please find the requested data attached.", "text/plain");
	            mp.addBodyPart(textPart);
	
	            MimeBodyPart attachment = new MimeBodyPart();
	            attachment.setFileName("export.csv");
	            attachment.setContent(data.toString(), "text/comma-separated-values");
	            mp.addBodyPart(attachment);
	
	            log.info("Sending the mail message");
	            msg.setContent(mp);
	            Transport.send(msg);
	
	        } catch (Exception ex) {
	            // ...
	        	log.severe(ex.toString());
	        }
		}
	}
	
	@Override
	public Set<String> getRequiredPermission(String method) {
		TreeSet<String> permissions = new TreeSet<String>();
		permissions.add(ADMIN_PERMISSION);
		permissions.add(TEACHER_PERMISSION);
		permissions.add(ALL_PERMISSION);	
		return permissions;
	}
}
