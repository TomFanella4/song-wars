package com.songwars.api.user.validate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.Base64;
import com.google.gson.Gson;
import com.songwars.api.utilities.Utilities;
import com.songwars.api.utilities.Validate;

public class ValidateUser implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private Context context;
	private LambdaLogger logger;
	
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
		
		
		Map<String, Object> json;
		Map<String, Object> params;
		Map<String, Object> querystring;
		Map<String, Object> response = new HashMap<String, Object>();
		// Required request fields:
		String name = null;
		String email = null;
		String cookie = "";
		String authorization_code = "";
		String scopes = "";
				
		/*
		 * 1. Check request body (validate) for proper format of fields:
		 */
		
		
	
		// Find Path:
		params = Validate.field(input, "params");
		querystring = Validate.field(params, "querystring");
		
		// Validate required fields:
		Validate.error(querystring);
		// May be able to feed these values into redirect_uri???
		//cookie = Validate.cookie(querystring); 
		//name = Validate.string(querystring, "name");
		authorization_code = Validate.string(querystring, "code");
		//scopes = Validate.string(querystring, "scopes");
		
		// Request Access and Refresh Tokens:		
		String url = "https://accounts.spotify.com/api/token";
		Map<String, String> headers = new HashMap<String, String>();
			headers.put("Authorization", "Basic " + Base64.encodeAsString(("52c0782611f74c95b5bd557ebfc62fcf" + ":" + "d0ced3f32e6e4d6b8dff5aa18026a613").getBytes()));
		Map<String, Object> body = new HashMap<String, Object>();
			body.put("grant_type", "authorization_code");
			body.put("code", authorization_code);
			body.put("redirect_uri", System.getenv("REDIRECT_URL"));
		HttpsURLConnection request = null;
		String access_token = null, expiration = null, refresh_token = null;
		try {
			request = Utilities.makeHttpsRequest(url, "POST", headers, body);
			
			if (request.getResponseCode() == 200) {
				
				Map<String, Object> response_body = (Map<String, Object>) new Gson().fromJson(new InputStreamReader(request.getInputStream()), HashMap.class);
				
				for (String key : response_body.keySet())
					logger.log(key + " : " + response_body.get(key));
				
				access_token = (String) response_body.get("access_token");
				expiration = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis() + ((Integer) response_body.get("expires_in")).intValue() * 1000));
				refresh_token = (String) response_body.get("refresh_token");
				
			} else {
				throw new RuntimeException("[InternalServerError] request to " + url + " was unsuccessful with: " + request.getResponseCode());
			}
			
		} catch (IOException ioe) {
			throw new RuntimeException("[InternalServerError] " + ioe.getStackTrace());
		} finally {
			if (request != null)
				request.disconnect();
		}
		
		
		// Check/Retrieve user's account info:
		if (cookie doesn't match???) 
		{
			url = "https://api.spotify.com/v1/me";
			headers = new HashMap<String, String>();
				headers.put("Authorization", "Bearer " + access_token);
			request = null;
			String name = null, email = null;
			try {
				request = Utilities.makeHttpsRequest(url, "POST", headers, body);
				
				if (request.getResponseCode() == 200) {
					
					Map<String, Object> response_body = (Map<String, Object>) new Gson().fromJson(new InputStreamReader(request.getInputStream()), HashMap.class);
					
					for (String key : response_body.keySet())
						logger.log(key + " : " + response_body.get(key));
					
					
					
				} else {
					throw new RuntimeException("[InternalServerError] request to " + url + " was unsuccessful with: " + request.getResponseCode());
				}
				
			} catch (IOException ioe) {
				throw new RuntimeException("[InternalServerError] " + ioe.getStackTrace());
			} finally {
				if (request != null)
					request.disconnect();
			}
		}
		
		
		// Database Connection:
		Connection con = Utilities.getRemoteConnection(context);
		
		// Does user have profile yet?
		try {
			String query = "INSERT INTO users (cookie, email, name, authorization_code, access_token, refresh_token, expiration) VALUES (" + cookie + ", " + email + ", " + name + ", " + authorization_code + ", " + access_token + ", " + refresh_token + ", " + expiration + ")";
			Statement statement = con.createStatement();
			statement.addBatch(query);
			statement.executeBatch();
			statement.close();

		} catch (SQLException ex) {
			// handle any errors
			logger.log("SQLException: " + ex.getMessage());
			logger.log("SQLState: " + ex.getSQLState());
			logger.log("VendorError: " + ex.getErrorCode());

			throw new RuntimeException("[InternalServerError] - SQL error occured.");
			
		} finally {
			context.getLogger().log("Closing the connection.");
			if (con != null)
				try {
					con.close();
				} catch (SQLException ignore) {
					throw new RuntimeException("[InternalServerError] - SQL error occured and is having trouble closing connection.");
				}
		} 
		
		// Add new cookie and authorization_code...
		return response;
		
	}

}