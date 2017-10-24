package com.songwars.api.brackets.current.vote;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.songwars.api.utilities.Rounds;
import com.songwars.api.utilities.Utilities;
import com.songwars.api.utilities.Validate;


public class GetVoteMatchup implements RequestHandler<Map<String, Object>, Map<String, Object>> {

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
		ArrayList<Integer> posRange = new ArrayList<Integer>();
		ArrayList<Integer> votesCasted = new ArrayList<Integer>();
		ArrayList<Integer> votesToCast = null;
		Set<Integer> votesToCastSet = null;
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
		round = Rounds.getFromMillis(System.currentTimeMillis());
		
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
			
			// Get the positions of votes that have already been cast:
			query = "SELECT * FROM users_last_week_bracket WHERE user_id=? AND bracket_id=? AND round=?";
			PreparedStatement pstatement = con.prepareStatement(query);
			pstatement.setString(1, user_id);
			pstatement.setString(2, bracket_id);
			pstatement.setInt(3, round);
			result = pstatement.executeQuery();
			
			// Fill position values:
			for (int i = 0; result.next(); i++)
				if (result.getInt("position") % 2 == 0)
					votesCasted.add(result.getInt("position"));
			result.close();
			statement.close();
			// Fill possible position values:
			for (int i = 0; i <= 8/round; i+=2)
				posRange.add(i);
			
			// Get random positions of songs yet to be cast
			votesToCastSet = new HashSet<Integer>(posRange);
			votesToCastSet.removeAll(new HashSet<Integer>(votesCasted));
			votesToCast = new ArrayList<Integer>(votesToCastSet);
			Collections.shuffle(votesToCast);
			oddVotesToCast = new ArrayList<Integer>();
			for (Integer i : votesToCast)
				oddVotesToCast.add(new Integer(i.intValue() - 1));
			
			// Get song details for the chosen matchup position:
			query = "SELECT * FROM last_week_bracket WHERE bracket_id=? AND round=? AND position IN ?";
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, bracket_id);
			pstatement.setInt(2, round);
			pstatement.setArray(3, con.createArrayOf("INT", votesToCast.toArray()));
			result = pstatement.executeQuery();
			

			//pos1 = Utilities.randomize((Integer[]) votesToCast.toArray()) * 2;
			ArrayList<HashMap<String, Object>> matchups = new ArrayList<HashMap<String,Object>>();
			for (Integer i : votesToCast) {
				
			}
				
			pos2 = Utilities.getOpponentsPosition(pos1);
			
			
			// Overly complicated procedure for making sure position and song_id match right.
			if (result.next())
				if (result.getInt("position") == pos1) {
					song_id1 = result.getString("id");
					if (result.next())
						song_id2 = result.getString("id");
					else
						throw new RuntimeException("[InternalServerError] - Position and round calculated returned only one song from bracket");
				}
				else {
					song_id2 = result.getString("id");
					if (result.next())
						song_id1 = result.getString("id");
					else
						throw new RuntimeException("[InternalServerError] - Position and round calculated returned only one song from bracket");
				}
			else
				throw new RuntimeException("[InternalServerError] - Position and round calculated returned no songs from bracket");
			

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
		
		response.put("user_id", user_id);
		response.put("access_token", access_token);
		response.put("bracket_id", bracket_id);
		response.put("round", round);
		response.put("position_1", pos1);
		response.put("song_id_1", song_id1);
		response.put("position_2", pos2);
		response.put("song_id_2", song_id2);
		return response;
		
	}

}