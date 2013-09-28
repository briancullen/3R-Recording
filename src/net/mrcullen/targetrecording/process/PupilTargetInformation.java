package net.mrcullen.targetrecording.process;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static net.mrcullen.targetrecording.OfyService.ofy;
import net.mrcullen.targetrecording.entities.PupilEntity;
import net.mrcullen.targetrecording.entities.PupilTargetEntity;
import net.mrcullen.targetrecording.entities.SubjectEntity;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.QueryKeys;

public class PupilTargetInformation {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(PupilTargetInformation.class.getName());
	
	public static List<PupilTargetEntity> findTargetInformation (HashMap<String,Object> parameters)
	{
		Query<PupilTargetEntity> query = ofy().load().type(PupilTargetEntity.class);
		for (String key : parameters.keySet())
		{
			query = query.filter(key, parameters.get(key));
		}
		
		return query.list();
	}
	
	public static List<PupilTargetEntity> findTargetInformationByPupil (PupilEntity ancestor, HashMap<String, Object> parameters)
	{
		Query<PupilTargetEntity> query = ofy().load().type(PupilTargetEntity.class).ancestor(Key.create(ancestor));
	
		for (String key : parameters.keySet())
		{
			query = query.filter(key, parameters.get(key));
		}
		
		return query.list();
	}

	public static List<PupilTargetEntity> findCurrentTargetInformationByPupil (PupilEntity ancestor, HashMap<String, Object> parameters)
	{
		Query<PupilTargetEntity> query = ofy().load().type(PupilTargetEntity.class)
				.ancestor(Key.create(ancestor)).filter("targetStage", ancestor.getForm().get().getStage());
		for (String key : parameters.keySet())
		{
			query = query.filter(key, parameters.get(key));
		}
		
		return query.list();
	}
	
	public static void removeTarget (Key<PupilTargetEntity> key)
	{
		TargetProgressInformation.removeAllTargetProgress(key);
		ofy().delete().entity(key).now();
	}
	
	public static void removeSubjectTarget (Key<PupilEntity> pupil, Key<SubjectEntity> subject)
	{
		QueryKeys<PupilTargetEntity> query = ofy().load().type(PupilTargetEntity.class).ancestor(pupil).filter("subject", subject).keys();
		List<Key<PupilTargetEntity>> list = query.list();
		
		if (list != null)
		{
			TargetProgressInformation.removeAllTargetProgress(pupil);
			ofy().delete().keys(list).now();
		}
	}
	
	public static void removePupilTargets (Key<PupilEntity> pupil)
	{
		ofy().delete().keys(ofy().load().ancestor(pupil).keys().list());
	}
	
	public static Key<PupilTargetEntity> saveTarget (PupilTargetEntity target)
	{
		return ofy().save().entity(target).now();
	}
	
	public static PupilTargetEntity getPupilTargetEntity (Key<PupilTargetEntity> targetKey)
	{
		return ofy().load().key(targetKey).now();
	}

	public static List<PupilTargetEntity> getAllPupilTargets() {
		return ofy().load().type(PupilTargetEntity.class).list();
	}

	public List<PupilTargetEntity> getPupilTargets(Key<PupilEntity> pupil) {
		return ofy().load().type(PupilTargetEntity.class).ancestor(pupil).list();
	}
}
