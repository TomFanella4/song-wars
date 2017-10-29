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
import com.mysql.jdbc.log.Log;
import com.songwars.api.utilities.Utilities;
import com.songwars.api.utilities.Validate;


public class PostVoteSelection implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private Context context;
	private LambdaLogger logger;
	
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
				
		
		// Response JSON:
		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> json = new HashMap<String, Object>();
		Map<String, Object> vote = new HashMap<String, Object>();

		this.context = context;
		this.logger = context.getLogger();
		
		//Local Variables
		String access_token = null;
		String user_id = null;
		String song_id = null;
		String bracket_id = null;
		Integer round = null;
		Integer position = null;
		Integer opponentPosition = null;
		
		//Get Path
		json = Validate.field(input, "body_json");
		vote = Validate.field(json, "vote");
		
		//Validate required fields for POST
		access_token = Validate.sqlstring(json, "access_token");
		user_id = Validate.sqlstring(json, "user_id");
		song_id = Validate.sqlstring(vote, "song_id");
		round = Validate.round(vote);
		position = Validate.position(vote);
		bracket_id = Validate.sqlstring(vote, "bracket_id");
		opponentPosition = Utilities.getOpponentsPosition(position);
		
//		{
//			“body_json”: {
//				“user_id”: “12156499783”,
//				“access_token’’: “BQAOqOLqygMQeopsvyIuXKmsMv7RGRvgF7x78ItSN33FsalPvahcb0W1mXQ3iy2soqE9SU6fK1bsCvn1z2QgLI9NjlCPpC5meCXGiIKdADTw8-4WYZi89S0iQRDCf0e7lpZTGh8Wbn5m_laFFth1”,
//				“vote”: {
//						“song_id”: “0M3adYbGtyRHACP86dey1H”,
//						“bracket_id”: “C9QCZq4U”,
//						“round”: 1,
//						“position”: 14
//					}
//				}
//		}
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
			
			//Check if the user has already voted on a song or it's opponent 
			query = "SELECT * FROM users_last_week_bracket WHERE user_id='" + user_id + "' AND bracket_id='" + bracket_id + "' AND round=" + round + " AND position=" + position + " OR position=" + opponentPosition;
			logger.log(query + "\n");
			statement = con.createStatement();
			result = statement.executeQuery(query);
			
			if (result.next())
				throw new RuntimeException("[BadRequest] Song has already been voted on by this user.");
			result.close();
			statement.close();
			
			//If not, add voted song to the list of already voted on songs
			query = "INSERT INTO users_last_week_bracket (user_id, bracket_id, position, round) VALUES ('" + user_id + "', '" + bracket_id + "', " + position + ", " + round + ")";
			logger.log(query + "\n");
			statement = con.createStatement();
			statement.execute(query);
			statement.close();
			
			//Vote on song and update vote count in last_week_bracket
			query = "UPDATE last_week_bracket SET votes=votes+1 WHERE id='" + song_id + "' AND round=" + round + " AND position=" + position;
			logger.log(query + "\n");
			statement = con.createStatement();
			statement.execute(query);
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
		//what to put in response?
//		response.put("round", round);
//		response.put("position", position);
//		response.put("song_id", song_id);
		response.put("status", "success");

		return response;
		
	}

}