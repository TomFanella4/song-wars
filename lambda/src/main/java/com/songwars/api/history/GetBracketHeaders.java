package com.songwars.api.history;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.songwars.api.utilities.Utilities;
import com.songwars.api.utilities.Validate;

public class GetBracketHeaders implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private Context context;
	private LambdaLogger logger;
	
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
				
		
		// Response JSON:
		this.context = context;
		this.logger = context.getLogger();
		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> querystring = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		
		
		
		ArrayList<Map<String, Object>> bracketHeaderList = new ArrayList<Map<String, Object>>();
		
		
		//Variables
		params = Validate.field(input, "params");
		querystring = Validate.field(params, "querystring");

		String access_token = Validate.sqlstring(querystring, "access_token");
		// Database Connection:
		Connection con = Utilities.getRemoteConnection(context);
		
		try {
			// Check for an authorized user:
			String query = "SELECT * FROM users WHERE access_token='" + access_token + "'";
			Statement statement = con.createStatement();
			ResultSet res = statement.executeQuery(query);
			if (!res.next())
				throw new RuntimeException("[Forbidden] Access token is not registered. Send user to login again.");
			res.close();
			statement.close();

			//Retrieve all the bracket headers
			query = "SELECT * FROM bracket_headers";
			statement = con.createStatement();
			res = statement.executeQuery(query);
			
//			if (!res.next()) {
//				throw new RuntimeException("[BadRequest] No bracket headers to retrieve.");
//			} else {
				while(res.next()) {
					LinkedHashMap<String, Object> header = new LinkedHashMap<String, Object>();
					String bracket_id = res.getString("bracket_id");
					String date = res.getString("date");
					header.put("bracket_id", bracket_id);
					header.put("date", date);
					bracketHeaderList.add(header);
				}
				

//			}
			//if response empty return an exception
			response.put("headers", bracketHeaderList);
			
			
			
			
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
