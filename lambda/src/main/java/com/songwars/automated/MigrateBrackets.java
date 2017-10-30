package com.songwars.automated;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.songwars.api.utilities.Utilities;

public class MigrateBrackets implements RequestHandler<Object, String> {
	
	private Context context;
	private LambdaLogger logger;
	
	
	@Override
	public String handleRequest(Object input, Context context) {
				
	
		// Init:
		this.context = context;
		this.logger = context.getLogger();
		// Local Variables:
		String bracket_id = null;
		ArrayList<HashMap<String, Object>> gems = new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> pop = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> song = null;
		// Database Connection:
		Connection con = Utilities.getRemoteConnection(context);
		
		try {
			// Make Insert and Delete ONE TRANSACTION:
			con.setAutoCommit(false);
			
			// Generate new bracket_id:
			bracket_id = Utilities.generateRandomString();
			
			// SELECT top recommendations from < X popularity:
			String query = "SELECT * FROM recommendations WHERE popularity<=70 ORDER BY count DESC LIMIT 8";
			PreparedStatement pstatement = con.prepareStatement(query);
			ResultSet result = pstatement.executeQuery();
			con.commit();
			
			while (result.next()) {
				song = new HashMap<String, Object>();
				song.put("id", result.getString("id"));
				song.put("name", result.getString("name"));
				song.put("popularity", new Integer(result.getInt("popularity")));
				song.put("preview_url", result.getString("preview_url"));
				song.put("album_name", result.getString("album_name"));
				song.put("album_image", result.getString("album_image"));
				song.put("artists_name", result.getString("artists_name"));
				
				gems.add(song);
			}
			result.close();
			pstatement.close();
			
			
			// SELECT leftover absolute top recommendations:
			query = "SELECT * FROM recommendations ORDER BY count DESC LIMIT 16";
			pstatement = con.prepareStatement(query);
			result = pstatement.executeQuery();
			con.commit();
			
			int i = 0;
			while (result.next() && i < 8) {
				
				// Check to see if any of these songs are one of the gems selected:
				boolean exists = false;
				for (HashMap<String, Object> s : gems)
					if (((String) s.get("id")).equals(result.getString("id"))) {
						System.out.println("Song Intersection!");
						exists = true;
						break;
					}
				// Do not add to pop list if they are already in gems:
				if (exists)
					continue;
				// Count songs that are added to pop (limit to 8)
				i++;
				
				song = new HashMap<String, Object>();
				song.put("id", result.getString("id"));
				song.put("name", result.getString("name"));
				song.put("popularity", new Integer(result.getInt("popularity")));
				song.put("preview_url", result.getString("preview_url"));
				song.put("album_name", result.getString("album_name"));
				song.put("album_image", result.getString("album_image"));
				song.put("artists_name", result.getString("artists_name"));
				
				pop.add(song);
			}
			result.close();
			pstatement.close();
			
			// Fill up 'top' or 'gem' songs from list if there aren't enough recommendations in category:
			if (gems.size() < 8)
				fillBackupGems(gems);
			if (pop.size() < 8)
				fillBackupPop(pop);
			
			// Shuffle matchups:
			Collections.shuffle(gems);
			Collections.shuffle(pop);
			
			// Insert songs into last_week_bracket:
			query = "INSERT INTO last_week_bracket (id, name, popularity, preview_url, album_name, album_image, artists_name, votes, bracket_id, round, position)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, 0, ?, 1, ?)";
			pstatement = con.prepareStatement(query);
			for (i = 0; i < pop.size(); i++) {		// Pop populates positions 1-8
				pstatement.setString(1, (String) pop.get(i).get("id"));
				pstatement.setString(2, (String) pop.get(i).get("name"));
				pstatement.setInt(3, ((Integer) pop.get(i).get("popularity")).intValue());
				pstatement.setString(4, (String) pop.get(i).get("preview_url"));
				pstatement.setString(5, (String) pop.get(i).get("album_name"));
				pstatement.setString(6, (String) pop.get(i).get("album_image"));
				pstatement.setString(7, (String) pop.get(i).get("artists_name"));
				pstatement.setString(8, bracket_id);
				pstatement.setInt(9,  i+1);
				pstatement.addBatch();
			}
			for (i = 0; i < gems.size(); i++) {		// Gems populate positions 9-16
				pstatement.setString(1, (String) gems.get(i).get("id"));
				pstatement.setString(2, (String) gems.get(i).get("name"));
				pstatement.setInt(3, ((Integer) gems.get(i).get("popularity")).intValue());
				pstatement.setString(4, (String) gems.get(i).get("preview_url"));
				pstatement.setString(5, (String) gems.get(i).get("album_name"));
				pstatement.setString(6, (String) gems.get(i).get("album_image"));
				pstatement.setString(7, (String) gems.get(i).get("artists_name"));
				pstatement.setString(8, bracket_id);
				pstatement.setInt(9,  i+9);
				pstatement.addBatch();
			}
			
			// Copy rows into bracket_history, then delete them from last_week_bracket:
			String insertquery = "INSERT INTO bracket_history SELECT * FROM last_week_bracket";
			String deletequery1 = "DELETE FROM last_week_bracket";
			String deletequery2 = "DELETE FROM users_last_week_bracket";
			String deletequery3 = "DELETE FROM users_recommendations";
			String deletequery4 = "DELETE FROM recommendations";
			PreparedStatement pstatement1 = con.prepareStatement(insertquery);
			PreparedStatement pstatement2 = con.prepareStatement(deletequery1);
			PreparedStatement pstatement3 = con.prepareStatement(deletequery2);
			PreparedStatement pstatement4 = con.prepareStatement(deletequery3);
			PreparedStatement pstatement5 = con.prepareStatement(deletequery4);
			pstatement1.execute();
			pstatement2.execute();
			//pstatement3.execute();
			//pstatement4.execute();
			//pstatement5.execute();
			
			int[] statuses = pstatement.executeBatch();
			
			// Execute whole transaction:
			con.commit();
			
			// Check for SQL Status of insertions:
			for (int s : statuses)
				if (statuses[s] == PreparedStatement.EXECUTE_FAILED)
					throw new RuntimeException("[InternalServerError] - Batch inserting new bracket songs Failed!");
			
			pstatement1.close();
			pstatement2.close();
			pstatement3.close();
			pstatement4.close();
			pstatement5.close();
			pstatement.close();
			

		} catch (SQLException ex) {
			// handle any errors
			ex.printStackTrace();
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
		
		return "Success:" + bracket_id;
	}
	
	
	
	public void fillBackupGems(ArrayList<HashMap<String, Object>> gems) {
		// 1. Never Gonna Give You Up - Rick Astley
		// 2. Shooting Stars - Bag Raiders
		// 3. All Star - Smash Mouth
		// 4. Bring Me To Life - Evanescence
		// 5. The Hamster Dance Song - Hampster Dance Masters?
		// 6. Chocolate Rain - Tay Zonday
		// 7. Here It Goes Again - OK Go
		// 8. Pants on the Ground - General Larry Platt
		
		/*HashMap<String, Object> song = new HashMap<String, Object>();
		song.put("id", );
		song.put("name", );
		song.put("popularity", new Integer());
		song.put("preview_url", );
		song.put("album_name", );
		song.put("album_image", );
		song.put("artists_name", );
		gems.put(song)*/
		
	}
	
	public void fillBackupPop(ArrayList<HashMap<String, Object>> pop) {
		// 1. Lose Yourself - Eminem
		// 2. Swish Swish - Katy Perry
		// 3. Hotline Bling - Drake
		// 4. The Cure - Lady Gaga
		// 5. rockstar - Post Malone, 21 Savage
		// 6. Havana - Camila Cabello, Young Thug
		// 7. I'm the One - DJ Khaled, Justin Beiber, Quevo, Chance the Rapper, Lil Wayne
		// 8. Don't Wanna Know - Maroon 5, Kendrick Lamar
		
		/*HashMap<String, Object> song = new HashMap<String, Object>();
		song.put("id", );
		song.put("name", );
		song.put("popularity", new Integer());
		song.put("preview_url", );
		song.put("album_name", );
		song.put("album_image", );
		song.put("artists_name", );
		gems.put(song)*/
		
	}
}
