package net.mrcullen.targetrecording.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableMap;
import com.googlecode.objectify.Key;

import net.mrcullen.targetrecording.GsonService;
import net.mrcullen.targetrecording.entities.FormEntity;
import net.mrcullen.targetrecording.entities.SubjectEntity;
import net.mrcullen.targetrecording.entities.TeacherEntity;
import net.mrcullen.targetrecording.process.FormInformation;
import net.mrcullen.targetrecording.process.PupilInformation;
import net.mrcullen.targetrecording.process.SubjectInformation;
import net.mrcullen.targetrecording.process.TeacherInformation;

@SuppressWarnings("serial")
public class ResetServlet extends HttpServlet {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ResetServlet.class.getName());
	
	private static Map<String, Integer> forms = new HashMap<String, Integer>() {{
		put("HU",Integer.valueOf(2013));
		put("HT",Integer.valueOf(2013));
		put("RH",Integer.valueOf(2013));
		put("BL",Integer.valueOf(2013));
		put("TG",Integer.valueOf(2013));
		put("WS",Integer.valueOf(2013));
		put("BH",Integer.valueOf(2013));
		put("VH",Integer.valueOf(2013));
		put("BR",Integer.valueOf(2013));
		put("GR",Integer.valueOf(2013));
		put("HL",Integer.valueOf(2012));
		put("WN",Integer.valueOf(2012));
		put("EM",Integer.valueOf(2012));
		put("SK",Integer.valueOf(2012));
		put("PS",Integer.valueOf(2012));
		put("MY",Integer.valueOf(2012));
		put("MC",Integer.valueOf(2012));
		put("RE",Integer.valueOf(2012));
		put("TN",Integer.valueOf(2011));
		put("DB",Integer.valueOf(2011));
		put("WD",Integer.valueOf(2011));
		put("CA",Integer.valueOf(2011));
		put("ES",Integer.valueOf(2011));
		put("PK",Integer.valueOf(2011));
		put("RB",Integer.valueOf(2011));
		put("ME",Integer.valueOf(2011));
		put("CR",Integer.valueOf(2011));
		put("FG",Integer.valueOf(2010));
		put("MR",Integer.valueOf(2010));
		put("CO",Integer.valueOf(2010));
		put("HK",Integer.valueOf(2010));
		put("EK",Integer.valueOf(2010));
		put("BT",Integer.valueOf(2010));
		put("BK",Integer.valueOf(2010));
		put("WT",Integer.valueOf(2010));
		put("BU",Integer.valueOf(2009));
		put("BD",Integer.valueOf(2009));
		put("SN",Integer.valueOf(2009));
		put("GB",Integer.valueOf(2009));
		put("MI",Integer.valueOf(2009));
		put("NM",Integer.valueOf(2009));
		put("TL",Integer.valueOf(2009));
		put("TR",Integer.valueOf(2009));
		put("SH",Integer.valueOf(2008));
		put("ML",Integer.valueOf(2008));
		put("RD",Integer.valueOf(2008));
		put("KG",Integer.valueOf(2008));
		put("DN",Integer.valueOf(2008));
		put("HN",Integer.valueOf(2008));
		put("CM",Integer.valueOf(2008));
		put("SG",Integer.valueOf(2007));
		put("DV",Integer.valueOf(2007));
		put("CL",Integer.valueOf(2007));
		put("LE",Integer.valueOf(2007));
		put("DL",Integer.valueOf(2007));
		put("LG",Integer.valueOf(2007));	
	}};
	
	private static final Map<String, Boolean> subjects = new HashMap<String, Boolean>() {{
		put("Art",false);
		put("PE",false);
		put("Maths",false);
		put("Science",false);
		put("Biology",false);
		put("Chemistry",false);
		put("Physics",false);
		put("English",false);
		put("Drama",false);
		put("Music",false);
		put("Business",false);
		put("Ecomomics",false);
		put("French",false);
		put("Spanish",false);
		put("Geography",false);
		put("History",false);
		put("Phil & Ethics",false);
		put("Computing",false);
		put("ICT",false);
		put("Food Tech",false);
		put("Res Materials",false);
		put("Textiles",false);
		put("Techonolgy",false);
		put("Music Technology", false);
		
		put("BTEC Media", true);
		put("ICT Nationals", true);
		put("ICT Technicals", true);
		put("Hairdressing", true);
		put("Health & Social Care", true);
		put("BTEC Business", true);
		put("Vocational Studies", true);
		put("Performing Arts", true);
		put("BTEC Sports", true);
		
		
	}};


	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
	}

	public void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
	}

	
	public void doDelete (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
	}
	
	public void doGet (HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		// If there are no teachers already in the database then make one.
		if ((TeacherInformation.getTeachers().size() == 0)
				&& (FormInformation.getForms().size() == 0)
				&& (SubjectInformation.getSubjects().size() == 0)
				&& (PupilInformation.getPupils().size() == 0))
		{
			TeacherEntity teacher = new TeacherEntity ("bcullen@rossettlearning.co.uk", "Mr B Cullen", "cl", true);
			Key<TeacherEntity> key = TeacherInformation.saveTeacher(teacher);
			resp.getWriter().print(GsonService.keyToJson(key));
			
			for (String formCode : forms.keySet())
			{
				FormEntity form = new FormEntity (formCode, forms.get(formCode));
				FormInformation.saveForm(form);
			}
			
			for (String subjectName : subjects.keySet())
			{
				SubjectEntity subject = new SubjectEntity (subjectName, subjects.get(subjectName));
				SubjectInformation.saveSubject(subject);
			}
		}
	}	
}
