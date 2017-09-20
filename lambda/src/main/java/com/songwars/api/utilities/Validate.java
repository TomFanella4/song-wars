package com.songwars.api.utilities;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.util.Base64;

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
	
	public static String cookie(Map<String, Object> json) {
		String cookie;

		if (!json.containsKey("cookie")
				|| !(json.get("cookie") instanceof String)
				|| !((cookie = (String) json.get("cookie")) != null)) {
		
			throw new RuntimeException("[BadRequest] Cookie key does not exist in request.");
		}
		
		// TODO: More validation required for this element?
		
		// Request Access and Refresh Tokens:
		String url = "https://accounts.spotify.com/api/token";
		Map<String, Object> headers = new HashMap<String, Object>();
			headers.put("Authorization", "Basic " + Base64.encodeAsString(((String) "52c0782611f74c95b5bd557ebfc62fcf" + ":" + "d0ced3f32e6e4d6b8dff5aa18026a613").getBytes()));
		Map<String, Object> body = new HashMap<String, Object>();
			body.put("grant_type", "authorization_code");
			body.put("code", "");
			body.put("redirect_uri", "");
		
		return cookie;
	}
	
	
	
}
