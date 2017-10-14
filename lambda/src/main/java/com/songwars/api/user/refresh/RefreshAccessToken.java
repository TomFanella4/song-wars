package com.songwars.api.user.refresh;

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
import com.songwars.api.user.validate.Gson;
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
		
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> querystring = new HashMap<String, Object>();
		String authorization_code = null;
		
		// Database Connection:
		Connection con = Utilities.getRemoteConnection(context);
		
		//Find Path and validate required fields
		params = Validate.field(input, "params");
		querystring = Validate.field(params, "querystring");
		authorization_code = Validate.string(querystring, "code");
		
		String url = "https://accounts.spotify.com/api/token";
		Map<String, String> headers = new HashMap<String, String>();
			headers.put("Authorization", "Basic " + Base64.encodeAsString((System.getenv("API_ID") + ":" + System.getenv("API_SECRET")).getBytes()));
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
		
		
		
		
		
		
		
		try {
				
			String query = "SELECT ... ???";
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
		
		return response;
		
	}

}