package com.songwars.api.bracket;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


import org.junit.validator.ValidateWith;

import com.amazonaws.protocol.json.JsonClientMetadata;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.mysql.fabric.xmlrpc.base.Data;
import com.songwars.api.utilities.Utilities;
import com.songwars.api.utilities.Validate;


public class RetrieveLastWeek implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private Context context;
	private LambdaLogger logger;
	
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
				
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		
		//Retrieve access token...need to grab the Validate class
		
//		Map<String, Object> json;
//		String access_token = null;
//		
//		json  = Validate.field(input, "body_json");
//		access_token = Validate.sqlstring(json, "access_token");
		
		Connection con = Utilities.getRemoteConnection(context);
		try {
			
//			//validate user
//			String query = "SELECT * FROM users WHERE access_token='" + access_token + "'";
//			Statement statement = con.createStatement();
//			ResultSet res = statement.executeQuery(query);
//
//			if (!res.next())
//				throw new RuntimeException("[Forbidden] Access token is not registered. Send user to login again.");
//			res.close();
//			statement.close();
			
			//Storage for match up data
			LinkedHashMap<String, Object> matchup = new LinkedHashMap<String, Object>();
			ArrayList<LinkedHashMap<String, Object>> matchupData = new ArrayList<>(); 
			ArrayList<Object> leftSide = new ArrayList<>();
			ArrayList<Object> rightSide = new ArrayList<>();
			ArrayList<Object> finals = new ArrayList<>();
			ArrayList<Object> winner = new ArrayList<>(); 
			String bracket_id = null;
			
			//Query for round 1 rows
			String query = "SELECT * FROM last_week_bracket WHERE round = 1";
			Statement statement = con.createStatement();
			ResultSet res = statement.executeQuery(query);
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
					data.put("id", id); data.put("name", name); data.put("popularity", popularity); data.put("uri", previewUrl); data.put("album", albumName); data.put("album_image", albumImage);
					data.put("artist", artistName); data.put("votes", votes); data.put("round", round); data.put("position", position);
					roundOneBracketData.add(data);
					bracket_id = bracketId;
					
			}
			
			if(roundOneBracketData.size() != 16) {
				throw new RuntimeException("[InternalServerError] Bracket is not fully populated!");
			}
			
			//Query for round 2 rows
			query = "SELECT * FROM last_week_bracket WHERE round = 2";
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
					data.put("id", id); data.put("name", name); data.put("popularity", popularity); data.put("uri", previewUrl); data.put("album", albumName); data.put("album_image", albumImage);
					data.put("artist", artistName); data.put("votes", votes); data.put("round", round); data.put("position", position);
					roundTwoBracketData.add(data);
			}
			
			//Query for round 3 rows
			query = "SELECT * FROM last_week_bracket WHERE round = 3";
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
					data.put("id", id); data.put("name", name); data.put("popularity", popularity); data.put("uri", previewUrl); data.put("album", albumName); data.put("album_image", albumImage);
					data.put("artist", artistName); data.put("votes", votes); data.put("round", round); data.put("position", position);
					roundThreeBracketData.add(data);
			}
			
			//Query for round 4
			query = "SELECT * FROM last_week_bracket WHERE round = 4";
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
					data.put("id", id); data.put("name", name); data.put("popularity", popularity); data.put("uri", previewUrl); data.put("album", albumName); data.put("album_image", albumImage);
					data.put("artist", artistName); data.put("votes", votes); data.put("round", round); data.put("position", position);
					roundFourBracketData.add(data);
			}
			
			//Calculate the winner
			query = "SELECT * FROM last_week_bracket WHERE round = 4 ORDER BY votes DESC LIMIT 1";
			statement = con.createStatement();
			res = statement.executeQuery(query);
			ArrayList<LinkedHashMap<String, Object>> winnerData = new ArrayList<>();
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
					data.put("id", id); data.put("name", name); data.put("popularity", popularity); data.put("uri", previewUrl); data.put("album", albumName); data.put("album_image", albumImage);
					data.put("artist", artistName); data.put("votes", votes); data.put("round", round); data.put("position", position);
					winnerData.add(data);
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
				matchupData.add(matchup);
				matchup = new LinkedHashMap<String, Object>();
				finals.add(matchupData);
				matchupData = new ArrayList<LinkedHashMap<String, Object>>();
			}
			
			//calculate the winner based off of the two finals
			if(!winnerData.isEmpty()) {
				matchup.put("Option1", winnerData.get(0));
				matchupData.add(matchup);
				matchup = new LinkedHashMap<String, Object>();
				winner.add(matchupData);
				matchupData = new ArrayList<LinkedHashMap<String, Object>>();
			}
			
			
			
			
			
			
			
			
			//Add bracket data to response
			response.put("BracketId", bracket_id);
			response.put("LeftSide", leftSide);
			response.put("RightSide", rightSide);
			if(!finals.isEmpty()) {
				response.put("Finals", finals);
			}
			if(!winner.isEmpty()) {
				response.put("Winner", winner);
			}
		
			
			//matchups.put("Option1", );
			
			//retrieve each row
//			while(res.next()) {
//				String name = res.getString("name");
//				int popularity = res.getInt("popularity");
//				String previewUrl = res.getString("preview_url");
//				String albumName = res.getString("album_name");
//				String artistName = res.getString("artists_name");
//				int votes = res.getInt("votes");
//				String bracketId = res.getString("bracket_id");
//				int round = res.getInt("round");
//				int position = res.getInt("position");
//				ArrayList<Object> data = new ArrayList<>();
//					data.add(name); data.add(popularity); data.add(previewUrl); data.add(albumName); data.add(artistName);
//					data.add(votes); data.add(bracketId); data.add(round); data.add(position);
//				response.put(res.getString("id"), data);
//			}
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

