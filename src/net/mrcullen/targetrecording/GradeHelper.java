package net.mrcullen.targetrecording;

import java.util.Arrays;
import java.util.Calendar;

public class GradeHelper {
	public static final String[] KS3_TARGET_GRADES = { "2c", "2b", "2a", "3c", "3b", "3a", "4c", "4b", "4a",
		"5c", "5b", "5a", "6c", "6b", "6a", "7c", "7b", "7a" };

	public static final String[] KS4_TARGET_GRADES = { "U", "G", "F", "E", "D", "C", "B", "A", "A*" };
	public static final String[] KS5_TARGET_GRADES = KS4_TARGET_GRADES;

	public static final String[] KS4_VOC_TARGET_GRADES = { "FOUNDATION", "LEVEL1", "PASS", "MERIT", "DISTINCTION", "DISTINCTION*" };
	public static final String[] KS5_VOC_TARGET_GRADES = {"PASS", "MERIT", "DISTINCTION", "DISTINCTION*" };
	
	public static int getStage (int yearGroup)
	{
		int stage = -1;
		if (yearGroup == 7 || yearGroup == 8)
			stage = yearGroup;
		else if (yearGroup > 8 && yearGroup < 12)
			stage = 4;
		else if (yearGroup > 11 && yearGroup < 14)
			stage = 5;
		
		return stage;
	}
	
	public static boolean isValidGradeForYear (String grade, boolean vocational, int yearGroup)
	{
		return isValidGradeForStage (grade, vocational, getStage(yearGroup));
	}
	
	public static boolean isValidGradeForStage (String grade, boolean vocational, int stage)
	{
		String[] validGrades = null;
		
		if (stage == 7 || stage == 8)
			validGrades = KS3_TARGET_GRADES;
		else
		{
			if (vocational)
			{
				if (stage == 4)
					validGrades = KS4_VOC_TARGET_GRADES;
				else if (stage == 5)
					validGrades = KS5_VOC_TARGET_GRADES;				
			}
			else
			{
				if (stage == 4)
					validGrades = KS4_TARGET_GRADES;
				else if (stage == 5)
					validGrades = KS5_TARGET_GRADES;
			}
		}
		
		if (validGrades == null)
			return false;
		
		return Arrays.asList(validGrades).contains(grade);
	}

}
