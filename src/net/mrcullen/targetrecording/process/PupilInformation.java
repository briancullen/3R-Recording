package net.mrcullen.targetrecording.process;

import static net.mrcullen.targetrecording.OfyService.ofy;

import java.util.List;

import com.googlecode.objectify.Key;

import net.mrcullen.targetrecording.entities.FormEntity;
import net.mrcullen.targetrecording.entities.PupilEntity;

public class PupilInformation {

	public static List<PupilEntity> getPupils ()
	{
		return ofy().load().type(PupilEntity.class).list();
	}
	
	public static List<PupilEntity> getPupilsByForm (Key<FormEntity> key)
	{
		return ofy().load().type(PupilEntity.class).filter("form", key).list();
	}
	
	public static List<PupilEntity> getPupilsByIntakeYear (int intakeYear)
	{
		
		return ofy().load().type(PupilEntity.class).filter("form in", 
				ofy().load().type(FormEntity.class).filter("intakeYear", intakeYear).keys()).list();
	}
	
	public static void removePupilsByForm (Key<FormEntity> key)
	{
		List<PupilEntity> pupils = getPupilsByForm(key);
		for (PupilEntity pupil : pupils)
		{
			PupilTargetInformation.removePupilTargets(Key.create(pupil));
		}
		ofy().delete().entities(getPupilsByForm(key)).now();
	}
	
	public static void removePupil (Key<PupilEntity> key)
	{
		PupilTargetInformation.removePupilTargets(key);
		ofy().delete().entity(key).now();
	}
	
	public static Key<PupilEntity> savePupil (PupilEntity update)
	{
		return ofy().save().entity(update).now();
	}
	
	public static PupilEntity getPupil (Key<PupilEntity> key)
	{
		return ofy().load().key(key).now();
	}

	public static PupilEntity getPupil(String pupilEmail) {
		return getPupil(Key.create(PupilEntity.class, pupilEmail));
	}
}
