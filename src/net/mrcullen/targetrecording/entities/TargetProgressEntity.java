package net.mrcullen.targetrecording.entities;

import net.mrcullen.targetrecording.GradeHelper;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class TargetProgressEntity {
	
	public static final String RESILIENCE_TYPE = "Resilience";
	public static final String RESPONSIBILITY_TYPE = "Responsibility";
	public static final String REFLECTION_TYPE = "Reflection";
	
	public static final String[] RECORD_TYPES = { RESILIENCE_TYPE, RESPONSIBILITY_TYPE, REFLECTION_TYPE };
	
	@Id private Long targetProgressId;
	@Load @Parent private Ref<PupilTargetEntity> pupilTarget;
	
	
	@Index private int yearGroup;
	private String currentLevel;
	private String nextSteps;
	@Index private String recordType;
	
	protected TargetProgressEntity ()
	{
		pupilTarget = null;
		yearGroup = 7;
		
		currentLevel = GradeHelper.KS3_TARGET_GRADES[0];
		nextSteps = null;
		recordType = RESPONSIBILITY_TYPE;
	}
	
	public TargetProgressEntity (PupilTargetEntity pupilTargetEntity, int year)
	{
		this(Ref.create(pupilTargetEntity), year);
	}
	
	public TargetProgressEntity (Ref<PupilTargetEntity> pupilTargetEntity, int year)
	{
		this();
		pupilTarget = pupilTargetEntity;
		
		setYearGroup (year);
	}
	
	public TargetProgressEntity (Long id, Ref<PupilTargetEntity> pupilEntity, int year)
	{
		this(pupilEntity, year);
		targetProgressId = id;
	}
	
	
	public PupilTargetEntity getPupilTarget () { return pupilTarget.get(); }
	
	public int getYearGroup () { return yearGroup; }
	public boolean setYearGroup (int year)
	{
		if ((year >= 7) && (year <= 13))
		{
			yearGroup = year;
			return true;
		}
		else return false;
	}
	
	public String getRecordType () { return recordType; }
	public boolean setRecordType (String newType)
	{
		for (int index = 0; index < RECORD_TYPES.length; index++)
		{
			if (newType.equalsIgnoreCase(RECORD_TYPES[index]))
			{
				recordType = RECORD_TYPES[index];
				return true;
			}
		}
		return false;
	}
	
	public String getCurrentLevel () { return currentLevel; }
	public boolean setLevel (String level)
	{
		boolean valid = GradeHelper.isValidGradeForYear(level, pupilTarget.get().isVocational(), yearGroup);
		if (valid)
		{
			currentLevel = level;
		}
		
		return valid;
	}

	public String getNextSteps () { return nextSteps; }
	public boolean setNextSteps (String steps)
	{
		if ((steps != null) && (steps.length() > 0))
		{
			this.nextSteps = steps;
			return true;
		}
		else return false;		
	}
}
