package com.songwars.api.song.recommend;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
				
		this.context = context;
		this.logger = context.getLogger();
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
		String user_id = null;
		String access_token = null;
		String song_id = null;
		String song_name = null;
		String song_preview_url = null;
		Integer song_popularity = null;
		String artists_name = null;
		String album_name = null;
		String album_image = null;
		
		// Find Path:
		
		json = Validate.field(input, "body_json");
		song = Validate.field(json, "song");
		album = Validate.field(song, "album");
		artists = Validate.field(song, "artists");
		
		// TODO: preview_url should really be uri, since it is actually a uri not a url.
		// Perform Validation of Input:
		user_id = Validate.sqlstring(json, "user_id");
		access_token = Validate.sqlstring(json, "access_token");
		song_id = Validate.sqlstring(song, "id");
		song_name = Validate.sqlstring(song, "name");
		song_preview_url = Validate.sqlstring(song, "preview_url");
		song_popularity = Validate.songPopularity(song);
		artists_name = Validate.sqlstring(artists, "name");
		album_name = Validate.sqlstring(album, "name");
		album_image = Validate.optsqlstring(album, "image_url");
		
		
		// Check for authorized user:
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
			
			// Check if recommendation was already made:
			query = "SELECT * FROM users_recommendations WHERE user_id='" + user_id + "' AND song_id='" + song_id + "'";
			statement = con.createStatement();
			result = statement.executeQuery(query);
			
			if (result.next())
				//throw new RuntimeException("[BadRequest] Song has already been recommended by this user.");
			result.close();
			statement.close();
			
			// Recommend song:
			query = "INSERT INTO users_recommendations (user_id, song_id) VALUES ('" + user_id + "', '" + song_id + "')";
			statement = con.createStatement();
			statement.addBatch(query);
			statement.executeBatch();
			statement.close();
			
			// Recommend song:
			if (album_image != null)
				query = "INSERT INTO recommendations (id, name, preview_url, popularity, artists_name, album_name, album_image, count) VALUES (?, '" + song_name + "', ?, ?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE count=count+1";
			else
				query = "INSERT INTO recommendations (id, name, preview_url, popularity, artists_name, album_name, count) VALUES (?, '" + song_name + "', ?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE count=count+1";
			PreparedStatement pstatement = con.prepareStatement(query);
			pstatement.setString(1, song_id);
			//pstatement.setString(2, song_name);
			pstatement.setString(2, song_preview_url);
			pstatement.setInt(3, song_popularity);
			pstatement.setString(4, artists_name);
			pstatement.setString(5, album_name);
			if (album_image != null)
				pstatement.setString(6, album_image);
			pstatement.execute();
			pstatement.close();
			
		} catch (SQLException ex) {
			// handle any errors
			logger.log("SQLException: " + ex.getMessage());
			logger.log("SQLState: " + ex.getSQLState());
			logger.log("VendorError: " + ex.getErrorCode());

			throw new RuntimeException("[InternalServerError] - " + ex.getMessage() + ", trace: " + ex.getStackTrace());
			
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