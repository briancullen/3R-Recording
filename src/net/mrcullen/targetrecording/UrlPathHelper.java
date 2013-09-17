package net.mrcullen.targetrecording;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.KeyFactory;
import com.googlecode.objectify.Key;

public class UrlPathHelper {
	private static final Logger log = Logger.getLogger(UrlPathHelper.class.getName());
	
	StringTokenizer tokens = null;
	
	public UrlPathHelper (String pathInfo)
	{
		if (pathInfo == null)
			pathInfo = "";
		
		tokens = new StringTokenizer (pathInfo, "/");
	}
	
	public long getIdFromPath ()
	{
		if (!tokens.hasMoreTokens())
			return -1;
		
		return getIdFromPath (tokens.nextToken());
	}

	public String getNameFromPath ()
	{
		if (!tokens.hasMoreTokens())
			return null;

		return tokens.nextToken();
	}
	
	public boolean isPathEmpty () { return !tokens.hasMoreElements(); }
	
	public static long getIdFromPath (String pathInfo)
	{
		long result = -1;
		
		try {
			result = Long.parseLong(getNameFromPath(pathInfo));
		}
		catch (Exception ex) { }
		
		return result;
	}
	
	public static String getNameFromPath (String pathInfo)
	{
		String result = null;
		if (pathInfo != null)
		{
			StringTokenizer tokens = new StringTokenizer (pathInfo, "/");
			if (tokens.hasMoreTokens())
			{
				result = tokens.nextToken();
			}
		}
		return result;
	}

	public static boolean isPathEmpty(String pathInfo) {
		return ((pathInfo == null) || (pathInfo.equalsIgnoreCase("/")));
	}
	
	@SuppressWarnings("rawtypes")
	public Key getKeyFromPath(String kind) {
		if (!tokens.hasMoreElements())
			return null;
		
		
		Key result = null;
		try {
			result = Key.create(KeyFactory.stringToKey(tokens.nextToken()));
			if (!result.getKind().equals(kind))
				result = null;

		} catch (Exception ex) { log.warning(ex.toString()); }

		return result;
	}

	@SuppressWarnings("rawtypes")
	public static Key getKeyFromPath(String pathInfo, String kind) {
		String name = getNameFromPath(pathInfo);
		if (name == null)
			return null;
		
		Key result = null;
		try {
			result = Key.create(KeyFactory.stringToKey(name));
			if (!result.getKind().equals(kind))
				result = null;
			
		} catch (Exception ex) { log.warning(ex.toString()); }
		
		return result;
	}
	
}
