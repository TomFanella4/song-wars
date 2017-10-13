package com.songwars.api.bracket;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.validator.ValidateWith;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.songwars.api.utilities.Utilities;

public class RetrieveLastWeek implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private Context context;
	private LambdaLogger logger;
	
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
				
		Map<String, Object> response = new HashMap<String, Object>();
		
		//Retrieve access token...need to grab the Validate class
		Map<String, Object> json = Validate.field(input, "body_json");
		String access_token = Validate.string(json, "access_token");
		
		Connection con = Utilities.getRemoteConnection(context);
		try {
			
			//validate user
			String query = "SELECT * FROM users WHERE access_token='" + access_token + "'";
			Statement statement = con.createStatement();
			ResultSet res = statement.executeQuery(query);

			if (!res.next())
				throw new RuntimeException("[Forbidden] Access token is not registered. Send user to login again.");
			res.close();
			statement.close();
			
			//Retrieve last week's bracket data
			query = "SELECT * FROM last_week_bracket";
			statement = con.createStatement();
			res = statement.executeQuery(query);
			//retrieve each row
			while(res.next()) {
				String name = res.getString("name");
				int popularity = res.getInt("popularity");
				String previewUrl = res.getString("preview_url");
				String albumName = res.getString("album_name");
				String artistName = res.getString("artists_name");
				int votes = res.getInt("votes");
				String bracketId = res.getString("bracket_id");
				ArrayList<Object> data = new ArrayList<>();
				data.add(name);
				data.add(popularity);
				data.add(previewUrl);
				data.add(albumName);
				data.add(artistName);
				data.add(votes);
				data.add(bracketId);
				response.put(res.getString("id"), data);
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
				
		return response;
	}

}

