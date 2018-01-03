package com.songwars.api.history;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.songwars.api.utilities.Utilities;
import com.songwars.api.utilities.Validate;

public class GetBracketHistory implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private Context context;
	private LambdaLogger logger;
	
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
				
		
		// Response JSON:
		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> querystring = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> path = new HashMap<String, Object>();
		
		//Local variables
		String access_token = null;
		String bracket_id = null;
		
		//Find path
		params = Validate.field(input, "params");
		path = Validate.field(params, "path");
		querystring = Validate.field(params, "querystring");

		
		//Validate input
		access_token = Validate.sqlstring(querystring, "access_token");
		bracket_id = Validate.sqlstring(path, "bracket-id");
		
		// Database Connection:
		Connection con = Utilities.getRemoteConnection(context);
		
		try {
			// Check for an authorized user:
			String query = "SELECT * FROM users WHERE access_token='" + access_token + "'";
			Statement statement = con.createStatement();
			ResultSet res = statement.executeQuery(query);

			if (!res.next())
				throw new RuntimeException("[Forbidden] Access token is not registered. Send user to login again.");
			res.close();
			statement.close();
			
			
			//copy and paste from get bracket but assume 30 rows 
			//Storage for match up data
			LinkedHashMap<String, Object> matchup = new LinkedHashMap<String, Object>();
			ArrayList<LinkedHashMap<String, Object>> matchupData = new ArrayList<>(); 
			ArrayList<Object> leftSide = new ArrayList<>();
			ArrayList<Object> rightSide = new ArrayList<>();
			ArrayList<Object> finals = new ArrayList<>();
			LinkedHashMap<String, Object> winner = new LinkedHashMap<String, Object>(); 
			
			//Query for round 1 rows
			query = "SELECT * FROM bracket_history WHERE round = 1 AND bracket_id='" + bracket_id + "'";
			statement = con.createStatement();
			res = statement.executeQuery(query);
			ArrayList<LinkedHashMap<String, Object>> roundOneBracketData = new ArrayList<>();
			while(res.next()) {
				String id = res.getString("id");
				String name = res.getString("name");
				int popularity = res.getInt("popularity");
				String previewUrl = res.getString("preview_url");
				String albumName = res.getString("album_name");
				String albumImage = res.getString("album_image");
				String artistName = res.getString("artists_name");
				int votes = res.getInt("votes");
				String bracketId = res.getString("bracket_id");
				int round = res.getInt("round");
				int position = res.getInt("position");
				LinkedHashMap<String,Object> data = new LinkedHashMap<>();
					data.put("id", id); data.put("name", name); data.put("popularity", popularity); data.put("preview_url", previewUrl); data.put("album_name", albumName); data.put("album_image", albumImage);
					data.put("artists_name", artistName); data.put("votes", votes); data.put("round", round); data.put("position", position);
					roundOneBracketData.add(data);
					bracket_id = bracketId;
					
			}
			
			if(roundOneBracketData.size() != 16) {
				throw new RuntimeException("[InternalServerError] Bracket could not be found!");
			}
			
			//Query for round 2 rows
			query = "SELECT * FROM bracket_history WHERE round = 2 AND bracket_id='" + bracket_id + "'";
			statement = con.createStatement();
			res = statement.executeQuery(query);
			ArrayList<LinkedHashMap<String, Object>> roundTwoBracketData = new ArrayList<>();
			while(res.next()) {
				String id = res.getString("id");
				String name = res.getString("name");
				int popularity = res.getInt("popularity");
				String previewUrl = res.getString("preview_url");
				String albumName = res.getString("album_name");
				String albumImage = res.getString("album_image");
				String artistName = res.getString("artists_name");
				int votes = res.getInt("votes");
				int round = res.getInt("round");
				int position = res.getInt("position");
				LinkedHashMap<String,Object> data = new LinkedHashMap<>();
					data.put("id", id); data.put("name", name); data.put("popularity", popularity); data.put("preview_url", previewUrl); data.put("album_name", albumName); data.put("album_image", albumImage);
					data.put("artists_name", artistName); data.put("votes", votes); data.put("round", round); data.put("position", position);
					roundTwoBracketData.add(data);
			}
			
			//Query for round 3 rows
			query = "SELECT * FROM bracket_history WHERE round = 3 AND bracket_id='" + bracket_id + "'";
			statement = con.createStatement();
			res = statement.executeQuery(query);
			ArrayList<LinkedHashMap<String, Object>> roundThreeBracketData = new ArrayList<>();
			while(res.next()) {
				String id = res.getString("id");
				String name = res.getString("name");
				int popularity = res.getInt("popularity");
				String previewUrl = res.getString("preview_url");
				String albumName = res.getString("album_name");
				String albumImage = res.getString("album_image");
				String artistName = res.getString("artists_name");
				int votes = res.getInt("votes");
				int round = res.getInt("round");
				int position = res.getInt("position");
				LinkedHashMap<String,Object> data = new LinkedHashMap<>();
					data.put("id", id); data.put("name", name); data.put("popularity", popularity); data.put("preview_url", previewUrl); data.put("album_name", albumName); data.put("album_image", albumImage);
					data.put("artists_name", artistName); data.put("votes", votes); data.put("round", round); data.put("position", position);
					roundThreeBracketData.add(data);
			}
			
			//Query for round 4
			query = "SELECT * FROM bracket_history WHERE round = 4 AND bracket_id='" + bracket_id + "'";
			statement = con.createStatement();
			res = statement.executeQuery(query);
			ArrayList<LinkedHashMap<String, Object>> roundFourBracketData = new ArrayList<>();
			while(res.next()) {
				String id = res.getString("id");
				String name = res.getString("name");
				int popularity = res.getInt("popularity");
				String previewUrl = res.getString("preview_url");
				String albumName = res.getString("album_name");
				String albumImage = res.getString("album_image");
				String artistName = res.getString("artists_name");
				int votes = res.getInt("votes");
				int round = res.getInt("round");
				int position = res.getInt("position");
				LinkedHashMap<String,Object> data = new LinkedHashMap<>();
					data.put("id", id); data.put("name", name); data.put("popularity", popularity); data.put("preview_url", previewUrl); data.put("album_name", albumName); data.put("album_image", albumImage);
					data.put("artists_name", artistName); data.put("votes", votes); data.put("round", round); data.put("position", position);
					roundFourBracketData.add(data);
			}
			
			//Query for winner
			query = "SELECT * FROM bracket_history WHERE round = 5 AND bracket_id='" + bracket_id + "'";
			statement = con.createStatement();
			res = statement.executeQuery(query);
			if(res.next()) {
				String id = res.getString("id");
				String name = res.getString("name");
				int popularity = res.getInt("popularity");
				String previewUrl = res.getString("preview_url");
				String albumName = res.getString("album_name");
				String albumImage = res.getString("album_image");
				String artistName = res.getString("artists_name");
				int votes = res.getInt("votes");
				int round = res.getInt("round");
				int position = res.getInt("position");
				winner.put("id", id); winner.put("name", name); winner.put("popularity", popularity); winner.put("preview_url", previewUrl); winner.put("album_name", albumName); winner.put("album_image", albumImage);
				winner.put("artists_name", artistName); winner.put("votes", votes); winner.put("round", round); winner.put("position", position);
			}
			

			//Round 1 Left Side
			for(int i = 0; i < 8; i = i + 2) {
				matchup.put("Option1", roundOneBracketData.get(i));
				matchup.put("Option2", roundOneBracketData.get(i + 1));
				matchupData.add(matchup);
				matchup = new LinkedHashMap<String, Object>();
			}

			leftSide.add(matchupData);
			matchupData = new ArrayList<LinkedHashMap<String, Object>>();

			//Round 1 Right Side
			for(int i = 8; i < 15; i = i + 2) {
				matchup.put("Option1", roundOneBracketData.get(i));
				matchup.put("Option2", roundOneBracketData.get(i + 1));
				matchupData.add(matchup);
				matchup = new LinkedHashMap<String, Object>();
			}
			rightSide.add(matchupData);
			matchupData = new ArrayList<LinkedHashMap<String, Object>>();

			if(!roundTwoBracketData.isEmpty()) {
			//Round 2 Left Side
			for(int i = 0; i < 4; i = i + 2) {
				matchup.put("Option1", roundTwoBracketData.get(i));
				matchup.put("Option2", roundTwoBracketData.get(i + 1));
				matchupData.add(matchup);
				matchup = new LinkedHashMap<String, Object>();
			}
			leftSide.add(matchupData);
			matchupData = new ArrayList<LinkedHashMap<String, Object>>();
			
			//Round 2 Right Side
			for(int i = 4; i < 7; i = i + 2) {
				matchup.put("Option1", roundTwoBracketData.get(i));
				matchup.put("Option2", roundTwoBracketData.get(i + 1));
				matchupData.add(matchup);
				matchup = new LinkedHashMap<String, Object>();
			}
			rightSide.add(matchupData);
			matchupData = new ArrayList<LinkedHashMap<String, Object>>();
			}
			
			if(!roundThreeBracketData.isEmpty()) {
				//Round 3 Left Side
		
				matchup.put("Option1", roundThreeBracketData.get(0));
				matchup.put("Option2", roundThreeBracketData.get(1));
				matchupData.add(matchup);
				matchup = new LinkedHashMap<String, Object>();
				leftSide.add(matchupData);
				matchupData = new ArrayList<LinkedHashMap<String, Object>>();
				
				//Round 3 Right Side
				matchup.put("Option1", roundThreeBracketData.get(2));
				matchup.put("Option2", roundThreeBracketData.get(3));
				matchupData.add(matchup);
				matchup = new LinkedHashMap<String, Object>();
				rightSide.add(matchupData);
				matchupData = new ArrayList<LinkedHashMap<String, Object>>();
			}
			
			if(!roundFourBracketData.isEmpty()) {
				//Round 4 (finals)
				matchup.put("Option1", roundFourBracketData.get(0));
				matchup.put("Option2", roundFourBracketData.get(1));
				finals.add(matchup);
				matchup = new LinkedHashMap<String, Object>();
				matchupData = new ArrayList<LinkedHashMap<String, Object>>();
			}
			
			//Add bracket data to response
			response.put("bracket_id", bracket_id);
			response.put("LeftSide", leftSide);
			response.put("RightSide", rightSide);
			if(!finals.isEmpty()) {
				response.put("Finals", finals);
			}
			if(!winner.isEmpty()) {
				response.put("Winner", winner);
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
