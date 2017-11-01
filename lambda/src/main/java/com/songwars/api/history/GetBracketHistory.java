package com.songwars.api.history;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.songwars.api.utilities.Utilities;
import com.songwars.api.utilities.Validate;

public class GetBracketHistory implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private Context context;
	private LambdaLogger logger;
	
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
				
		
		// Response JSON:
		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> json = new HashMap<String, Object>();
		Map<String, Object> querystring = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		
		//Local variables
		String access_token = null;
		String bracket_id = null;
		
		//Find path
		json = Validate.field(input, "body_json");
		params = Validate.field(input, "params");
		querystring = Validate.field(params, "querystring");
		
		
		//Validate input
		access_token = Validate.sqlstring(json, "access_token");
		bracket_id = Validate.sqlstring(querystring, "bracket-id");
		
		// Database Connection:
		Connection con = Utilities.getRemoteConnection(context);
		
		try {
			// Check for an authorized user:
			String query = "SELECT * FROM users WHERE access_token='" + access_token + "'";
			Statement statement = con.createStatement();
			ResultSet result = statement.executeQuery(query);

			if (!result.next())
				throw new RuntimeException("[Forbidden] Access token is not registered. Send user to login again.");
			result.close();
			statement.close();
			
			
			//copy and paste from get bracket but assume 30 rows 
			

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
