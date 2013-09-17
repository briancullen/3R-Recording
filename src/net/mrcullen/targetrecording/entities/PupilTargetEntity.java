package net.mrcullen.targetrecording.entities;

import net.mrcullen.targetrecording.GradeHelper;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class PupilTargetEntity {
	
	@Id private Long pupilTargetId;
	@Load @Parent private Ref<PupilEntity> pupil;
	@Load @Index private Ref<SubjectEntity> subject;
	@Index private int keyStage;

	private String threeLevelsTargetGrade;
	private String fourLevelsTargetGrade;
	private String fiveLevelsTargetGrade;
	
	protected PupilTargetEntity ()
	{
		pupil = null;
		subject = null;
		keyStage = 3;
		
		threeLevelsTargetGrade = GradeHelper.KS3_TARGET_GRADES[3];
		fourLevelsTargetGrade = GradeHelper.KS3_TARGET_GRADES[4];
		fiveLevelsTargetGrade = GradeHelper.KS3_TARGET_GRADES[5];
	}
	
	public PupilTargetEntity (PupilEntity pupilEntity, SubjectEntity subjectEntity, int keyStage)
	{
		this(Ref.create(pupilEntity), Ref.create(subjectEntity), keyStage);
	}
	
	public PupilTargetEntity (Ref<PupilEntity> pupilEntity, Ref<SubjectEntity> subjectEntity, int keyStage)
	{
		this();
		pupil = pupilEntity;
		subject = subjectEntity;
		
		setKeyStage(keyStage);
	}
	
	public PupilTargetEntity (Long id, Ref<PupilEntity> pupilEntity, Ref<SubjectEntity> subjectEntity, int keyStage)
	{
		this(pupilEntity, subjectEntity, keyStage);
		pupilTargetId = id;
	}
	
	public PupilEntity getPupil() { return pupil.get(); }
	public SubjectEntity getSubject() { return subject.get(); }
	public int getKeyStage () { return keyStage; }
	public boolean setKeyStage (int stage)
	{
		if ((stage >= 3) && (stage <= 5))
		{
			keyStage = stage;
			return true;
		}
		else return false;
	}
	
	public String getThreeLevelsTargetGrade () { return threeLevelsTargetGrade; }
	public String getFourLevelsTargetGrade () { return fourLevelsTargetGrade; }
	public String getFiveLevelsTargetGrade () { return fiveLevelsTargetGrade; }

	public boolean setTargetGrades (String threeLevels, String fourLevels, String fiveLevels)
	{
		boolean vocational = isVocational();
		if (GradeHelper.isValidGradeForKeyStage(threeLevels, vocational, keyStage))
		{
			threeLevelsTargetGrade = threeLevels;
		}
		else return false;
		
		if (GradeHelper.isValidGradeForKeyStage(fourLevels, vocational, keyStage))
		{
			fourLevelsTargetGrade = fourLevels;
		}
		else return false;
		
		if (GradeHelper.isValidGradeForKeyStage(fiveLevels, vocational, keyStage))
		{
			fiveLevelsTargetGrade = fiveLevels;
		}
		else return false;
		
		return true;
	}
	
	public boolean isVocational() {
		return subject.get().isVocational();
	}
}
