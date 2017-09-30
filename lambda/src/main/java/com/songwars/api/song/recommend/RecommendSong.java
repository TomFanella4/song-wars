package com.songwars.api.song.recommend;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.junit.validator.ValidateWith;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.songwars.api.utilities.Utilities;
import com.songwars.api.utilities.Validate;

public class RecommendSong implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private Context context;
	private LambdaLogger logger;
	
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
				
		
		// Function Logic Status:
		boolean user_exists = false;
		// JSON:
		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> params;
		Map<String, Object> querystring;
		Map<String, Object> json;
		Map<String, Object> song;
		Map<String, Object> artists;
		Map<String, Object> album;
		// Local Input Variables:
		String access_token = null;
		String song_id = null;
		String song_name = null;
		String song_preview_url = null;
		Integer song_popularity = null;
		String artists_name = null;
		String album_name = null;
		
		// Find Path:
		
		json = Validate.field(input, "json-body");
		song = Validate.field(json, "song");
		album = Validate.field(song, "album");
		artists = Validate.field(song, "artists");
		
		// Perform Validation of Input:
		access_token = Validate.string(json, "access_token");
		song_id = Validate.string(song, "song_id");
		song_name = Validate.string(song, "name");
		song_preview_url = Validate.string(song, "preview_url");
		song_popularity = Validate.songPopularity(song);
		artists_name = Validate.string(artists, "artists_name");
		album_name = Validate.string(album, "album_name");
		
		
		// Check for authorized user:
		Connection con = Utilities.getRemoteConnection(context);
		try {
			
			String query = "SELECT * FROM users WHERE access_token='" + access_token + "'";
			Statement statement = con.createStatement();
			ResultSet result = statement.executeQuery(query);
			statement.close();

			if (!result.next())
				throw new RuntimeException("[Forbidden] Access token is not registered. Send user to login again.");
		
			// Recommend song:
			query = "INSERT INTO recommendations (id, name, preview_url, popularity, artists_name, album_name, count) VALUES ('" + song_id + "', '" + song_name + "', '" + song_preview_url + "', '" + song_popularity + "', '" + artists_name + "', '" + album_name + "', 1) ON DUPLICATE KEY UPDATE count=count+1"; // TODO: Test this MYSQL syntax in workbench first!
			statement = con.createStatement();
			statement.addBatch(query);
			statement.executeBatch();
		
			
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
		
		response.put("status", "success");
		return response;
		
	}

}