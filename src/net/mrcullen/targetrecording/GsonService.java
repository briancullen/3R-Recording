package net.mrcullen.targetrecording;


import java.lang.reflect.Type;
import java.util.List;

import com.google.appengine.api.datastore.KeyFactory;

import net.mrcullen.targetrecording.entities.FormEntity;
import net.mrcullen.targetrecording.entities.SubjectEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

public class GsonService {
	
	@SuppressWarnings("rawtypes")
	private static class RefSerializer implements JsonSerializer<Ref> {
		  public JsonElement serialize(Ref src, Type typeOfSrc, JsonSerializationContext context) {
			  JsonElement result = null;

			  com.google.appengine.api.datastore.Key refKey = src.getKey().getRaw();			  
			  if (typeOfSrc.toString().contains("Ref<net.mrcullen.targetrecording.entities.SubjectEntity>"))
			  {
				  JsonObject objResult = new JsonObject(); 
				  objResult.addProperty("id", ((SubjectEntity)src.get()).getSubjectId());
				  objResult.addProperty("name", ((SubjectEntity)src.get()).getName());
				  objResult.addProperty("vocational", ((SubjectEntity)src.get()).isVocational());
				  objResult.addProperty("key", KeyFactory.keyToString(refKey));
				  result = objResult;
			  }
			  else if (typeOfSrc.toString().contains("Ref<net.mrcullen.targetrecording.entities.FormEntity>"))
			  {
				  JsonObject objResult = new JsonObject ();
				  objResult.addProperty("formCode", ((FormEntity)src.get()).getFormCode());
				  objResult.addProperty("intakeYear", ((FormEntity)src.get()).getIntakeYear());
				  objResult.addProperty("key", KeyFactory.keyToString(refKey));
				  result = objResult;
			  }
			  else
			  {
				  result =  new JsonPrimitive (KeyFactory.keyToString(refKey));
			  }
			  
			  return result;
		  }	
	}
	
	@SuppressWarnings("rawtypes")
	private static class RefDeserializer implements JsonDeserializer<Ref> {
		@SuppressWarnings("unchecked")
		public Ref deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			// TODO Auto-generated method stub
			
			String keyString = null;
			if (typeOfT.toString().contains("Ref<net.mrcullen.targetrecording.entities.SubjectEntity>")
					|| typeOfT.toString().contains("Ref<net.mrcullen.targetrecording.entities.FormEntity>"))
			{
				keyString = ((JsonObject)json).get("key").getAsString();
			}
			else
			{
				keyString = json.getAsString();
			}
			
			Key myKey = Key.create(KeyFactory.stringToKey(keyString));
			return Ref.create(myKey);
		}
	}
	
	public static Gson gson = new GsonBuilder().serializeNulls()
			.registerTypeAdapter(Ref.class, new RefSerializer())
			.registerTypeAdapter(Ref.class, new RefDeserializer()).create();
	
	public static String entityToJson (List objList)
	{
		JsonArray jsonArray = new JsonArray();
		for (Object obj : objList)
		{
			jsonArray.add(entityToJsonTree(obj));
		}
		return gson.toJson(jsonArray);
	}
	
	public static String entityToJson (Object obj)
	{
		return gson.toJson(entityToJsonTree(obj));
	}
	
	public static JsonObject entityToJsonTree (Object obj)
	{
		JsonObject jsonObject = (JsonObject) gson.toJsonTree(obj);
		jsonObject.add("key", new JsonPrimitive (KeyFactory.keyToString(Key.create(obj).getRaw())));
		return jsonObject;
	}
	
	@SuppressWarnings("rawtypes")
	public static String keyToJson (Key key)
	{
		return gson.toJson(KeyFactory.keyToString(key.getRaw()));
	}
}
