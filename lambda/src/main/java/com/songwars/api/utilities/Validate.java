package com.songwars.api.utilities;

import java.util.Map;


public class Validate {

	static public Map<String, Object> field(Map<String, Object> json, String fieldname)
	{
		Map<String, Object> path;
		if(!json.containsKey(fieldname)
				|| !(json.get(fieldname) instanceof Map)
				|| !((path = (Map<String, Object>) json.get(fieldname)) != null)) {
			throw new RuntimeException("[BadRequest] Could not access \'" + fieldname + "\' field of AWS transformed JSON.");
		}
		else 
			return path;
	}
	
	static public String string(Map<String, Object> json, String fieldname) 
	{
		String string;
		if (!json.containsKey(fieldname)
				|| !(json.get(fieldname) instanceof String)
				|| !((string = (String) json.get(fieldname)) != null)) {
			throw new RuntimeException("[BadRequest] Could not access \'" + fieldname + "\' string of AWS transformed JSON.");
		}
		else 
			return string;
	}
	
	public static void error(Map<String, Object> json) {
		String error;
		
		if (!json.containsKey("error")
				|| !(json.get("error") instanceof String)
				|| !((error = (String) json.get("error")) != null)) {
			return;
		}
		else 
			throw new RuntimeException("[Unauthorized] Error \'" + error + "\' occurred during service authentication and user cannot be accepted.");
	}
	
	public static String email(Map<String, Object> json) {
		String email;

		if (!json.containsKey("email")
				|| !(json.get("email") instanceof String)
				|| !((email = (String) json.get("email")) != null)) {
		
			throw new RuntimeException("[BadRequest] Email key does not exist in request.");
		}
		
		if (email.length() > 45
				// Regex is supposed to loosely match general email form.
				|| !email.matches(".{3,}@.{3,}\\..{2,}")) {
			
			throw new RuntimeException("[BadRequest] Email not formatted correctly: \'" + email + "\'.");
		}
		
		return email;
	}
	
	public static int songPopularity(Map<String, Object> json) {
		Integer popularity;
		
		if (!json.containsKey("popularity")
				|| !(json.get("popularity") instanceof String)
				|| !((popularity = (Integer) json.get("popularity")) != null)) {
		
			throw new RuntimeException("[BadRequest] Popularity key does not exist for song in request.");
		}
		if (popularity.intValue() > 100 || popularity.intValue() < 0)
			throw new RuntimeException("[BadRequest] Song Popularity must be between 0 and 100 inclusive.");
		
		return popularity.intValue();
	}
	
	
	
}
