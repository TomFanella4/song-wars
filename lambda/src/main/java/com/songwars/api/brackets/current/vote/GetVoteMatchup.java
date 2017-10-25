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
import com.songwars.api.utilities.Matchup;
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
		ArrayList<Matchup> matchups = new ArrayList<Matchup>();
		Set<Integer> votesToCastSet = null;
		int round = 0;
		
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
				votesCasted.add(result.getInt("position"));
			
			result.close();
			statement.close();
			// Fill possible position values:
			for (int i = 1; i <= 8/round; i++)
				posRange.add(i);
			
			// Get random positions of songs yet to be cast
			votesToCastSet = new HashSet<Integer>(posRange);
			votesToCastSet.removeAll(new HashSet<Integer>(votesCasted));
			votesToCast = new ArrayList<Integer>(votesToCastSet);
			Collections.shuffle(votesToCast);
			
			// Make paired list of songs (ie. matchups)
			boolean exists = false;
			for (Integer i : votesToCast) {
				for (Matchup m : matchups) {
					if (m.contains(i.intValue())) {
						exists = true;
						break;
					}
				}
				if (exists == false)
					matchups.add(new Matchup(round, i));
				exists = false;
			}
						
			// Get song details for the remaining matchup positions:
			query = "SELECT * FROM last_week_bracket WHERE bracket_id=? AND round=? AND position IN ?";
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, bracket_id);
			pstatement.setInt(2, round);
			pstatement.setArray(3, con.createArrayOf("INT", votesToCast.toArray()));
			result = pstatement.executeQuery();
			
			while (result.next()) {
				int pos = result.getInt("position");
				for (Matchup m : matchups) {
					if (m.contains(pos)) {
						m.setId(pos, result.getString("id"));
						m.setName(pos, result.getString("name"));
						m.setPopularity(pos, result.getInt("popularity"));
						m.setPreview_Url(pos, result.getString("preview_url"));
						m.setAlbum_name(pos, result.getString("album_name"));
						m.setAlbum_image(pos, result.getString("album_image"));
						m.setArtists_name(pos, result.getString("artists_name"));
						m.setVotes(pos, result.getInt("votes"));
						m.setBracket_Id(pos, result.getString("bracket_id"));
						break;
					}
				}
			}

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
		ArrayList<Map<String, Object>> matchups_maps = new ArrayList<Map<String, Object>>();
		for (Matchup m : matchups)
			matchups_maps.add(m.toMap());
		response.put("matchups", matchups_maps);
			
		return response;
		
	}

}