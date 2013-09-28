package net.mrcullen.targetrecording.entities;

import java.util.Calendar;

import net.mrcullen.targetrecording.GradeHelper;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class FormEntity {
	@Id private Long formId;
	@Index private String formCode;
	@Index private int intakeYear;
	
	protected FormEntity () { formCode = null; intakeYear = -1; }
	
	public FormEntity (String formCode, int intakeYear)
	{
		this();
		setFormCode(formCode);
		setIntakeYear(intakeYear);
	}
	
	public String getFormCode () { return formCode; }
	public int getIntakeYear () { return intakeYear; }
	public void setIntakeYear (int newIntakeYear)
	{
		this.intakeYear = newIntakeYear;
	}
	
	public int getYearGroup () {
		Calendar cal = Calendar.getInstance();
		int currentYear = cal.get(Calendar.YEAR);
		int currentMonth = cal.get(Calendar.MONTH);
		
		if (currentMonth < 8)
			currentYear --;

		return currentYear - intakeYear + 7;
	}
	
	public int getStage() {
		return GradeHelper.getStage(getYearGroup());
	}

	public void setFormCode(String newFormCode) {
		if ((newFormCode != null) && (!newFormCode.isEmpty()))
			formCode = newFormCode.toUpperCase();
	}
}
