package net.mrcullen.targetrecording;

import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.googlecode.objectify.Key;

import net.mrcullen.targetrecording.entities.PupilTargetEntity;
import net.mrcullen.targetrecording.entities.TargetProgressEntity;
import net.mrcullen.targetrecording.process.TargetProgressInformation;

public class PupilRecordHelper {

	public static JsonArray constructRecords (PupilTargetEntity target, List<TargetProgressEntity> progress)
	{
		JsonElement baseElement = GsonService.entityToJsonTree(target);
		
		JsonArray result = new JsonArray();
		for (TargetProgressEntity entity : progress)
		{
			JsonObject currentRecord = new JsonObject();
			currentRecord.add("target", baseElement);
			currentRecord.add("progress", GsonService.entityToJsonTree(entity));
			result.add(currentRecord);
		}
		
		return result;
	}
	
	public static JsonElement constructRecords (PupilTargetEntity target, TargetProgressEntity progress)
	{
		JsonObject baseElement = new JsonObject();
		baseElement.add("target", GsonService.entityToJsonTree(target));
		baseElement.add("progress", GsonService.entityToJsonTree(progress));
		
		return baseElement;
	}
	
	public static JsonElement constructRecord (TargetProgressEntity progress)
	{
		return constructRecords (progress.getPupilTarget(), progress);
	}
	
	public static JsonElement constructRecords (List<TargetProgressEntity> progress)
	{
		JsonArray result = new JsonArray();
		
		for (TargetProgressEntity entity : progress)
		{
			result.add(constructRecords (entity.getPupilTarget(), entity));
		}
		
		return result;
	}
	
	public static JsonElement constructRecordsFromTargets (List<PupilTargetEntity> targets)
	{
		JsonArray result = new JsonArray();
		
		for (PupilTargetEntity entity : targets)
		{
			List<TargetProgressEntity> list = TargetProgressInformation.findProgressInformationByAncestor(Key.create(entity), new HashMap<String,Object>());
			result.addAll(constructRecords(entity, list));
		}
		
		return result;
	}
	
	
}
