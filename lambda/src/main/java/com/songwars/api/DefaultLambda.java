package com.songwars.api;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.songwars.api.utilities.Utilities;


public class DefaultLambda implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private Context context;
	private LambdaLogger logger;
	
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
		
		this.context = context;
		this.logger = context.getLogger();
		
		System.out.println(input.toString());
		logger.log(input.toString());

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("value", "ok");
		
		if (((String) ((Map<String, Object>) ((Map<String, Object>) input.get("params")).get("querystring")).get("error")).equals("yes"))
			throw new RuntimeException("[PageNotFound]");
		else
			return response;
		
		/*
		// Response JSON:
		Map<String, Object> response = new HashMap<String, Object>();
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

			//return generate500(ex.getMessage());
			
		} finally {
			context.getLogger().log("Closing the connection.");
			if (con != null)
				try {
					con.close();
				} catch (SQLException ignore) {
					//return generate500("SQL error.");
				}
		}
		
		return response;
		*/
	}

}