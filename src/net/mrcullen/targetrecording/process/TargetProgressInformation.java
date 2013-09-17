package net.mrcullen.targetrecording.process;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static net.mrcullen.targetrecording.OfyService.ofy;
import net.mrcullen.targetrecording.entities.PupilTargetEntity;
import net.mrcullen.targetrecording.entities.TargetProgressEntity;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

public class TargetProgressInformation {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(TargetProgressInformation.class.getName());
	
	public static List<TargetProgressEntity> findProgressInformation (HashMap<String,Object> parameters)
	{
		Query<TargetProgressEntity> query = ofy().load().type(TargetProgressEntity.class);
		for (String key : parameters.keySet())
		{
			query = query.filter(key, parameters.get(key));
		}
		
		return query.list();
	}
	
	@SuppressWarnings("rawtypes")
	public static List<TargetProgressEntity> findProgressInformationByAncestor (Key ancestor, HashMap<String, Object> parameters)
	{
		Query<TargetProgressEntity> query = ofy().load().type(TargetProgressEntity.class).ancestor(ancestor);
		for (String key : parameters.keySet())
		{
			query = query.filter(key, parameters.get(key));
		}
		
		return query.list();
	}
	
	@SuppressWarnings("rawtypes")
	public static void removeAllTargetProgress (Key target)
	{
		ofy().delete().keys(ofy().load().type(PupilTargetEntity.class).ancestor(target).keys().list());
	}

	public static Key<TargetProgressEntity> saveTargetProgress (TargetProgressEntity target)
	{
		return ofy().save().entity(target).now();
	}
	
	public static TargetProgressEntity getTargetProgressEntity (Key<TargetProgressEntity> targetKey)
	{
		return ofy().load().key(targetKey).now();
	}

	public static void removeTargetProgress(Key<TargetProgressEntity> key) {
		ofy().delete().key(key);
		
	}

	public static List<TargetProgressEntity> getAllTargetProgress()
	{
		return ofy().load().type(TargetProgressEntity.class).list();
	}
}
