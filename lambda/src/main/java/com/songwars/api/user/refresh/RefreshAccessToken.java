package com.songwars.api.user.refresh;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
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


public class RefreshAccessToken implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private Context context;
	private LambdaLogger logger;
	
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
				
		//grab new access and refresh token..put in user table, return spotify userid
		
		
		// Response JSON:
		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> json = new HashMap<String, Object>();
		String access_token = null;
		String expiration = null;
		String user_id = null;
		String refresh_token = null;
		
		//Find Path and validate required fields, check request body
		json = Validate.field(input, "body_json");
		user_id = Validate.sqlstring(json, "user_id");
		access_token = Validate.sqlstring(json, "access_token");
		
		// Database Connection:
		Connection con = Utilities.getRemoteConnection(context);
		
		try {
			
			String query = "SELECT * FROM users WHERE access_token='" + access_token + "'";
			Statement statement = con.createStatement();
			ResultSet result = statement.executeQuery(query);

			if (!result.next()) {
				throw new RuntimeException("[Forbidden] Access token is not registered. Send user to login again.");
			} else {
				refresh_token = result.getString("refresh_token");
			}
			
		} catch (SQLException ex) {
			// handle any errors
			logger.log("SQLException: " + ex.getMessage());
			logger.log("SQLState: " + ex.getSQLState());
			logger.log("VendorError: " + ex.getErrorCode());

			throw new RuntimeException("[InternalServerError] - " + ex.getMessage() + ", trace: " + ex.getStackTrace());
		}
		
		//Request a new access token
		String url = "https://accounts.spotify.com/api/token";
		Map<String, String> headers = new HashMap<String, String>();
			headers.put("Authorization", "Basic " + Base64.encodeAsString((System.getenv("API_ID") + ":" + System.getenv("API_SECRET")).getBytes()));
		Map<String, Object> body = new HashMap<String, Object>();
			body.put("grant_type", "refresh_token");
			body.put("refresh_token", refresh_token);
		HttpsURLConnection request = null;
		
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

		con = Utilities.getRemoteConnection(context);
		try {
				
			// Update user tokens 
			String query = "UPDATE users SET access_token='" + access_token + "', expiration='" + expiration + "' WHERE id='" + user_id + "'";
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
		
		//Insert user id and access token into the response
		response.put("user_id", user_id);
		response.put("access_token", access_token);
		response.put("refesh_token", refresh_token);
		return response;
		
	}

}