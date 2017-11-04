package com.songwars.api.user.validate;

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
import com.amazonaws.util.IOUtils;
import com.google.gson.Gson;
import com.songwars.api.utilities.Utilities;
import com.songwars.api.utilities.Validate;

public class ValidateUser implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private Context context;
	private LambdaLogger logger;
	
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
		
		this.context = context;
		this.logger = context.getLogger();
		
		Map<String, Object> json;
		Map<String, Object> params;
		Map<String, Object> querystring;
		Map<String, Object> response = new HashMap<String, Object>();
		Connection con = null;
		// Required request fields:
		String id = null;
		String name = null;
		String email = null;
		String authorization_code = null;

		/*
		 * 1. Check request body (validate) for proper format of fields:
		 */
	
		// Find Path:
		//json = Validate.field(input, "json_body");
		params = Validate.field(input, "params");
		querystring = Validate.field(params, "querystring");
		
		// Validate required fields:
		authorization_code = Validate.string(querystring, "code");
		
		
		
		// Request Access and Refresh Tokens:
		String url = "https://accounts.spotify.com/api/token";
		Map<String, String> headers = new HashMap<String, String>();
			headers.put("Authorization", "Basic " + Base64.encodeAsString((System.getenv("API_ID") + ":" + System.getenv("API_SECRET")).getBytes()));
		Map<String, Object> body = new HashMap<String, Object>();
			body.put("grant_type", "authorization_code");
			body.put("code", authorization_code);
			body.put("redirect_uri", System.getenv("REDIRECT_URL"));
		logger.log("Redirect Url: " + System.getenv("REDIRECT_URL") + "\n");
		HttpsURLConnection request = null;
		String access_token = null, expiration = null, refresh_token = null;
		try {
			request = Utilities.makeHttpsRequest(url, "POST", headers, body);
			
			if (request.getResponseCode() == 200) {
				
				Map<String, Object> response_body = (Map<String, Object>) new Gson().fromJson(new InputStreamReader(request.getInputStream()), HashMap.class);
				
				access_token = (String) response_body.get("access_token");
				expiration = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis() + ((Double) response_body.get("expires_in")).intValue() * 1000));
				refresh_token = (String) response_body.get("refresh_token");
				
			} else {
				throw new RuntimeException("[InternalServerError] request to " + url + " was unsuccessful with: " + request.getResponseCode() + ". Error body: " + ", " + IOUtils.toString(request.getErrorStream()));
			}
			
		} catch (IOException ioe) {
			throw new RuntimeException("[InternalServerError] " + ioe.getMessage() + ", trace: " + ioe.getStackTrace());
		} finally {
			if (request != null)
				request.disconnect();
			request = null;
		}
		
		
		// Retrieve user information from Spotify.
		url = "https://api.spotify.com/v1/me";
		headers = new HashMap<String, String>();
			headers.put("Authorization", "Bearer " + access_token);
		body = null;
		request = null;
		try {
			request = Utilities.makeHttpsRequest(url, "GET", headers, body);
			
			if (request.getResponseCode() == 200) {
				
				Map<String, Object> response_body = (Map<String, Object>) new Gson().fromJson(new InputStreamReader(request.getInputStream()), HashMap.class);
				
				id = (String) response_body.get("id");
				email = (String) response_body.get("email");
				name = (String) response_body.get("display_name");
				
			} else {
				throw new RuntimeException("[InternalServerError] request to " + url + " was unsuccessful with: " + request.getResponseCode() + ". Error body: " + ", " + IOUtils.toString(request.getErrorStream()));
			}
			
		} catch (IOException ioe) {
			throw new RuntimeException("[InternalServerError] " + ioe.getMessage() + ", " + ioe.getStackTrace());
		} finally {
			if (request != null)
				request.disconnect();
		}
		
			
		con = Utilities.getRemoteConnection(context);
		try {
				
			// Insert new user or update credentials if user_id already exists
			String query = "INSERT INTO users (id, email, name, authorization_code, access_token, refresh_token, expiration) VALUES ('" + id + "', '" + email + "', '" + name + "', '" + authorization_code + "', '" + access_token + "', '" + refresh_token + "', '" + expiration + "') ON DUPLICATE KEY UPDATE authorization_code='" + authorization_code + "', access_token='" + access_token + "', refresh_token='" + refresh_token + "', expiration='" + expiration + "'";
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
		
		
		response.put("name", name);
		response.put("access_token", access_token);
		response.put("user_id", id);
		return response;
		
	}

}