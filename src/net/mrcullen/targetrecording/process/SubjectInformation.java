package net.mrcullen.targetrecording.process;

import static net.mrcullen.targetrecording.OfyService.ofy;

import java.util.List;

import com.googlecode.objectify.Key;

import net.mrcullen.targetrecording.entities.SubjectEntity;

public class SubjectInformation {

	public static List<SubjectEntity> getSubjects ()
	{
		return ofy().load().type(SubjectEntity.class).list();
	}
	
	public static void removeSubject (long subjectId)
	{
		removeSubject (Key.create(SubjectEntity.class, subjectId));
	}
	
	public static void removeSubject (Key<SubjectEntity> key)
	{
		ofy().delete().entity(key);
	}
	
	public static Key<SubjectEntity> saveSubject (SubjectEntity update)
	{
		return ofy().save().entity(update).now();
	}
	
	public static SubjectEntity getSubject (long subjectId)
	{
		return getSubject (Key.create(SubjectEntity.class, subjectId));
	}
	
	public static SubjectEntity getSubject (Key<SubjectEntity> key)
	{
		return ofy().load().key(key).now();
	}
}
