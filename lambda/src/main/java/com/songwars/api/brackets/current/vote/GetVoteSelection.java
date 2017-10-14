package com.songwars.api.brackets.current.vote;

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


public class GetVoteSelection implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private Context context;
	private LambdaLogger logger;
	
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
				
		
		this.context = context;
		this.logger = context.getLogger();
		// Function Logic Status:
		boolean user_exists = false;
		// JSON:
		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> params;
		Map<String, Object> querystring;
		Map<String, Object> json;
		// Local Input Variables:
		String user_id = null;
		String access_token = null;
		String song_id1 = null;
		String song_id2 = null;
		
		// Database Connection:
		Connection con = Utilities.getRemoteConnection(context);
		
		try {
			// Check for authorized user:
			String query = "SELECT * FROM users WHERE access_token='" + access_token + "'";
			Statement statement = con.createStatement();
			ResultSet result = statement.executeQuery(query);

			if (!result.next())
				throw new RuntimeException("[Forbidden] Access token is not registered. Send user to login again.");
			result.close();
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