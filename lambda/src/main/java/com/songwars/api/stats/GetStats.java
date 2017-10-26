package com.songwars.api.stats;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.songwars.api.utilities.Matchup;
import com.songwars.api.utilities.Utilities;
import com.songwars.api.utilities.Validate;

public class GetStats implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private Context context;
	private LambdaLogger logger;
	
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
				
		
		this.context = context;
		this.logger = context.getLogger();
		// JSON:
		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> params;
		Map<String, Object> path;
		Map<String, Object> querystring;
		// Local Input Variables:
		String user_id = null;
		String access_token = null;
		String bracket_id = null;
		ArrayList<Integer> posRange = new ArrayList<Integer>();
		ArrayList<Integer> votesCasted = new ArrayList<Integer>();
		ArrayList<Integer> votesToCast = null;
		ArrayList<Matchup> matchups = new ArrayList<Matchup>();
		Set<Integer> votesToCastSet = null;
		int round = 0;
		
		// Find Path:
		params = Validate.field(input, "params");
		path = Validate.field(params, "path");
		querystring = Validate.field(params, "querystring");
		
		// Perform Validation of Input:
		user_id = Validate.sqlstring(querystring, "user_id");
		access_token = Validate.sqlstring(querystring, "access_token");
		bracket_id = Validate.sqlstring(path, "bracket-id");

		// @Obsolete - Round is now identified through its own sql query.
		// Get which round is supposed to be voted on today:
		//round = Rounds.getFromMillis(System.currentTimeMillis());
		//round = Rounds.getFromEnviron();
		
		// Database Connection:
		Connection con = Utilities.getRemoteConnection(context);
		
		try {
			
			// Check for an authorized user:
			String query = "SELECT * FROM users WHERE access_token='" + access_token + "'";
			Statement statement = con.createStatement();
			ResultSet result = statement.executeQuery(query);
			
		} catch (SQLException ex) {
			// handle any errors
			logger.log("SQLException: " + ex.getMessage() + "\n");
			logger.log("SQLState: " + ex.getSQLState() + "\n");
			logger.log("VendorError: " + ex.getErrorCode() + "\n");
			logger.log("Trace: ");
			ex.printStackTrace();

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
		
		response.put("user_id", user_id);
		response.put("access_token", access_token);
		response.put("bracket_id", bracket_id);
		response.put("round", round);
		ArrayList<Map<String, Object>> matchups_maps = new ArrayList<Map<String, Object>>();
		for (Matchup m : matchups)
			matchups_maps.add(m.toMap());
		response.put("matchups", matchups_maps);
			
		return response;
		
	}

}
