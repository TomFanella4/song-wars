package com.songwars.api.stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
	
	/**
	 * The current stats to be displayed on the website are: 
	 * 
	 * # of recommendations for this week (inserted from migrate brackets!)
	 *
	 * id = 1, name = "recommendations", value = INT
	 * 
	 * # of votes counted
	 * id = 2, name = "votes", value = INT
	 * 
	 * # Most nominated artists
	 * id = 3, name = "[artist1]", value = number of songs recommended
	 * id = 4, name = "[artist2]", value = number of songs rec...
	 * ...
	 * id = 12, name = "[artist10]", value = number of songs recommended
	 * 
	 * # Top winning songs
	 * id = 13, name = "[songname1]", value = number of wins
	 * id = 14, name = "[songname2]", value = number of ...
	 * ...
	 * id = 22, name = "[songname10]", value = number of wins
	 * 
	 */
	
	
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
		ArrayList<HashMap<String, Object>> artists = new ArrayList<HashMap<String, Object>>(10);
		ArrayList<HashMap<String, Object>> songs = new ArrayList<HashMap<String, Object>>(10);
		
		// Find Path:
		params = Validate.field(input, "params");
		path = Validate.field(params, "path");
		querystring = Validate.field(params, "querystring");
		
		// Perform Validation of Input:
		user_id = Validate.sqlstring(querystring, "user_id");
		access_token = Validate.sqlstring(querystring, "access_token");
		
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

			// Get stats information:
			query = "SELECT * FROM stats";
			PreparedStatement pstatement = con.prepareStatement(query);
			result = pstatement.executeQuery();
			
			logger.log("artists: " + artists.size() + "\n");
			logger.log("songs: " + songs.size() + "\n");
			
			
			while (result.next()) {
				int id = result.getInt("id");
				if (id == 1)
					response.put("recommendations", result.getInt("value"));
				else if (id == 2)
					response.put("votes", result.getInt("value"));
				else if (id <= 12) {
					HashMap<String, Object> artist = new HashMap<String, Object>();
					artist.put("name", result.getString("name"));
					artist.put("count", result.getInt("value"));
					artists.add(id - 3, artist);
				}
				else {
					HashMap<String, Object> song = new HashMap<String, Object>();
					song.put("name", result.getString("name"));
					song.put("count", result.getInt("value"));
					songs.add(id - 13, song);
				}
			}
			
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
		response.put("top_artists", artists);
		response.put("top_songs", songs);
			
		return response;
		
	}

}
