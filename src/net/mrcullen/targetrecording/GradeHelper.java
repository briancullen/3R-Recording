package net.mrcullen.targetrecording;

import java.util.Arrays;
import java.util.Calendar;

public class GradeHelper {
	public static final String[] KS3_TARGET_GRADES = { "2c", "2b", "2a", "3c", "3b", "3a", "4c", "4b", "4a",
		"5c", "5b", "5a", "6c", "6b", "6a", "7c", "7b", "7a" };

	public static final String[] KS4_TARGET_GRADES = { "U", "G", "F", "E", "D", "C", "B", "A", "A*" };
	public static final String[] KS5_TARGET_GRADES = KS4_TARGET_GRADES;

	public static final String[] KS4_VOC_TARGET_GRADES = {"PASS", "MERIT", "DISTINCTION", "DISTINCTION*" };
	public static final String[] KS5_VOC_TARGET_GRADES = {"PASS", "MERIT", "DISTINCTION", "DISTINCTION*" };
	
	public static int getKeyStage (int yearGroup)
	{
		int keyStage = -1;
		if (yearGroup == 7 || yearGroup == 8)
			keyStage = 3;
		else if (yearGroup > 8 && yearGroup < 12)
			keyStage = 4;
		else if (yearGroup > 11 && yearGroup < 14)
			keyStage = 5;
		
		return keyStage;
	}
	
	public static boolean isValidGradeForYear (String grade, boolean vocational, int yearGroup)
	{
		return isValidGradeForKeyStage (grade, vocational, getKeyStage(yearGroup));
	}
	
	public static boolean isValidGradeForKeyStage (String grade, boolean vocational, int keyStage)
	{
		String[] validGrades = null;
		
		if (keyStage == 3)
			validGrades = KS3_TARGET_GRADES;
		else
		{
			if (vocational)
			{
				if (keyStage == 4)
					validGrades = KS4_VOC_TARGET_GRADES;
				else if (keyStage == 5)
					validGrades = KS5_VOC_TARGET_GRADES;				
			}
			else
			{
				if (keyStage == 4)
					validGrades = KS4_TARGET_GRADES;
				else if (keyStage == 5)
					validGrades = KS5_TARGET_GRADES;
			}
		}
		
		if (validGrades == null)
			return false;
		
		return Arrays.asList(validGrades).contains(grade);
	}

}
