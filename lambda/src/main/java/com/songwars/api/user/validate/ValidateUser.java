package com.songwars.api.user.validate;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
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
		String email = "";
		String name = "";
		String cookie = "";
		String authorization_code = "";
		String scopes = "";
				
		/*
		 * 1. Check request body (validate) for proper format of fields:
		 */
		
		
	
		// Find Path:
		params = Validate.field(input, "params");
		querystring = Validate.field(params, "querystrings");
		
		// Validate required fields:
		Validate.error(querystring);
		// May be able to feed these values into redirect_uri???
		//cookie = Validate.cookie(querystring); 
		//name = Validate.string(querystring, "name");
		authorization_code = Validate.string(querystring, "code");
		//scopes = Validate.string(querystring, "scopes");
		
		
		
		
		
		// Database Connection:
		Connection con = Utilities.getRemoteConnection(context);
		
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