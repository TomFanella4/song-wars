package com.songwars.api.brackets.current.vote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.songwars.api.utilities.Rounds;
import com.songwars.api.utilities.Utilities;
import com.songwars.api.utilities.Validate;


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
		String bracket_id = null;
		int round = 0;
		int pos1 = 0;
		int pos2 = 0;
		String song_id1 = null;
		String song_id2 = null;
		
		// Find Path:
		json = Validate.field(input, "body_json");
		
		// Perform Validation of Input:
		user_id = Validate.sqlstring(json, "user_id");
		access_token = Validate.sqlstring(json, "access_token");
		bracket_id = Validate.sqlstring(json, "bracket_id");
		
		// Get which round is supposed to be voted on today:
		// TODO: HARD: How to randomize users' matchups, but efficiently find remaining matchups without duplicating...??? 
		round = Rounds.getFromMillis(System.currentTimeMillis());
		pos1 = Utilities.generateRandomPosition(round);
		pos2 = Utilities.getOpponentsPosition(pos1);
		
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
			
			if (result.next())
				throw new RuntimeException("[BadRequest] Song has already been recommended by this user.");
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