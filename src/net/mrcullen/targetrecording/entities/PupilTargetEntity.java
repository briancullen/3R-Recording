package net.mrcullen.targetrecording.entities;

import net.mrcullen.targetrecording.GradeHelper;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Parent;

@Entity
@Cache
public class PupilTargetEntity {
	
	@Id private Long pupilTargetId;
	@Load @Parent private Ref<PupilEntity> pupil;
	@Load @Index private Ref<SubjectEntity> subject;
	@Index private int targetStage;

	private String threeLevelsTargetGrade;
	private String fourLevelsTargetGrade;
	private String fiveLevelsTargetGrade;
	
	protected PupilTargetEntity ()
	{
		pupil = null;
		subject = null;
		targetStage = 7;
		
		threeLevelsTargetGrade = GradeHelper.KS3_TARGET_GRADES[3];
		fourLevelsTargetGrade = GradeHelper.KS3_TARGET_GRADES[4];
		fiveLevelsTargetGrade = GradeHelper.KS3_TARGET_GRADES[5];
	}
	
	public PupilTargetEntity (PupilEntity pupilEntity, SubjectEntity subjectEntity, int stage)
	{
		this(Ref.create(pupilEntity), Ref.create(subjectEntity), stage);
	}
	
	public PupilTargetEntity (Ref<PupilEntity> pupilEntity, Ref<SubjectEntity> subjectEntity, int stage)
	{
		this();
		pupil = pupilEntity;
		subject = subjectEntity;
		
		setStage(stage);
	}
	
	public PupilTargetEntity (Long id, Ref<PupilEntity> pupilEntity, Ref<SubjectEntity> subjectEntity, int stage)
	{
		this(pupilEntity, subjectEntity, stage);
		pupilTargetId = id;
	}
	
	public PupilEntity getPupil() { return pupil.get(); }
	public SubjectEntity getSubject() { return subject.get(); }
	public int getStage () { return targetStage; }
	public boolean setStage (int stage)
	{
		if ((stage == 4) || (stage == 5) || (stage == 7) || (stage == 8))
		{
			targetStage = stage;
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
		if (GradeHelper.isValidGradeForStage(threeLevels, vocational, targetStage))
		{
			threeLevelsTargetGrade = threeLevels;
		}
		else return false;
		
		if (GradeHelper.isValidGradeForStage(fourLevels, vocational, targetStage))
		{
			fourLevelsTargetGrade = fourLevels;
		}
		else return false;
		
		if (GradeHelper.isValidGradeForStage(fiveLevels, vocational, targetStage))
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
