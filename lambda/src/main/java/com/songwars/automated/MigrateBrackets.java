package com.songwars.automated;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
			
			// Put new bracket's info into bracket_headers table:
			String query = "INSERT INTO bracket_headers (bracket_id, type, date) VALUES (?, ?, ?)";
			PreparedStatement pstatement = con.prepareStatement(query);
			pstatement.setString(1, bracket_id);
			pstatement.setString(2, "Primary");
			pstatement.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
			pstatement.execute();
			con.commit();
			
			

			// Get recommendations count for stats table:
			query = "SELECT count FROM recommendations";
			pstatement = con.prepareStatement(query);
			ResultSet result = pstatement.executeQuery();
			con.commit();
			
			int recommendation_total = 0;
			while (result.next()) {
				recommendation_total += result.getInt("count");
			}
			result.close();
			pstatement.close();
			
			// Insert recommendations total into stats table:
			query = "INSERT INTO stats (id, name, value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE value=?";
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, 1);
			pstatement.setString(2, "recommendations");
			pstatement.setInt(3, recommendation_total);
			pstatement.setInt(4, recommendation_total);
			pstatement.execute();
			con.commit();
			
			pstatement.close();
			
			
			
			// SELECT top recommendations from < X popularity:
			query = "SELECT * FROM recommendations WHERE popularity<=50 ORDER BY count DESC LIMIT 8";
			pstatement = con.prepareStatement(query);
			result = pstatement.executeQuery();
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
			

			// Get bracket_id of old bracket so we can set it to "History" from "Primary"
			query = "SELECT bracket_id FROM last_week_bracket LIMIT 1";
			PreparedStatement pstatement7 = con.prepareStatement(query);
			result = pstatement7.executeQuery();
			con.commit();
			
			if (result.next())
				bracket_id = result.getString("bracket_id");
			
			result.close();
			pstatement7.close();
			
			
			// Copy rows into bracket_history, then delete them from last_week_bracket:
			String insertquery1 = "INSERT INTO bracket_history SELECT * FROM last_week_bracket";
			String insertquery2 = "INSERT INTO users_votes_history SELECT * FROM users_last_week_bracket";
			String insertquery3 = "INSERT INTO users_recommendations_history SELECT * FROM users_recommendations";
			String updatequery = "UPDATE bracket_headers SET type='History' WHERE bracket_id=?";
			String deletequery1 = "DELETE FROM last_week_bracket";
			String deletequery2 = "DELETE FROM users_last_week_bracket";
			String deletequery3 = "DELETE FROM users_recommendations";
			String deletequery4 = "DELETE FROM recommendations";
			PreparedStatement pstatement1 = con.prepareStatement(insertquery1);
			PreparedStatement pstatement2 = con.prepareStatement(insertquery2);
			PreparedStatement pstatement3 = con.prepareStatement(insertquery3);
			PreparedStatement pstatement4 = con.prepareStatement(updatequery);
			PreparedStatement pstatement5 = con.prepareStatement(deletequery1);
			PreparedStatement pstatement6 = con.prepareStatement(deletequery2);
			pstatement7 = con.prepareStatement(deletequery3);
			PreparedStatement pstatement8 = con.prepareStatement(deletequery4);
			pstatement4.setString(1, bracket_id);
			pstatement1.execute();
			pstatement2.execute();
			pstatement3.execute();
			pstatement4.execute();
			pstatement5.execute();
			pstatement6.execute();
			pstatement7.execute();
			pstatement8.execute();
            
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
			pstatement6.close();
			pstatement7.close();
			pstatement8.close();
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
		
		switch (gems.size()) {
		case 0:
			HashMap<String, Object> song = new HashMap<String, Object>();
			song.put("id", "2aYguRih2pKaKXiW7UFrbA");
			song.put("name", "Pants On The Ground");
			song.put("popularity", new Integer(21));
			song.put("preview_url", "spotify:track:2aYguRih2pKaKXiW7UFrbA");
			song.put("album_name", "Pants On The Ground");
			song.put("album_image", "https://i.scdn.co/image/5ea91e5178e40bdb162b41d2830bb9074a0ff6ff");
			song.put("artists_name", "General Larry Platt");
			gems.add(song);
		case 1:
			song = new HashMap<String, Object>();
			song.put("id", "1pHP4JeQV9wDx87D6qH9hD");
			song.put("name", "Here It Goes Again");
			song.put("popularity", new Integer(62));
			song.put("preview_url", "spotify:track:1pHP4JeQV9wDx87D6qH9hD");
			song.put("album_name", "Oh No");
			song.put("album_image", "https://i.scdn.co/image/e5d8ae33539f5d928ffa49c3b34b81b757c64493");
			song.put("artists_name", "OK Go");
			gems.add(song);
		case 2:
			song = new HashMap<String, Object>();
			song.put("id", "3sJ1HwrSpTVosPhJHFbRLK");
			song.put("name", "Chocolate Rain");
			song.put("popularity", new Integer(36));
			song.put("preview_url", "spotify:track:3sJ1HwrSpTVosPhJHFbRLK");
			song.put("album_name", "Chocolate Rain");
			song.put("album_image", "https://i.scdn.co/image/68f21baf058b93e801f62df9587d13c4ea4c9a38");
			song.put("artists_name", "Tay Zonday");
			gems.add(song);
		case 3:
			song = new HashMap<String, Object>();
			song.put("id", "0t8vGVdJaU93uQYMNqViyf");
			song.put("name", "The Hamster Dance Song");
			song.put("popularity", new Integer(43));
			song.put("preview_url", "spotify:track:0t8vGVdJaU93uQYMNqViyf");
			song.put("album_name", "The Hampster Dance........and Friends");
			song.put("album_image", "https://i.scdn.co/image/bcadf40e1ac9c02f000505fb078a68f0fbc0acfb");
			song.put("artists_name", "Hampster Dance Masters");
			gems.add(song);
		case 4:
			song = new HashMap<String, Object>();
			song.put("id", "0COqiPhxzoWICwFCS4eZcp");
			song.put("name", "Bring Me To Life");
			song.put("popularity", new Integer(71));
			song.put("preview_url", "spotify:track:0COqiPhxzoWICwFCS4eZcp");
			song.put("album_name", "Fallen");
			song.put("album_image", "https://i.scdn.co/image/b7874d9ac6948de4945c2c3d44739dc549a481f7");
			song.put("artists_name", "Evanescence");
			gems.add(song);
		case 5:
			song = new HashMap<String, Object>();
			song.put("id", "19fm0eNKTeg1JbHCHwPhLe");
			song.put("name", "All Star");
			song.put("popularity", new Integer(51));
			song.put("preview_url", "spotify:track:19fm0eNKTeg1JbHCHwPhLe");
			song.put("album_name", "All Star Smash Hits");
			song.put("album_image", "https://i.scdn.co/image/b3fc81a516d5eceb2ee8cfafca1f2f00ec30672e");
			song.put("artists_name", "Smash Mouth");
			gems.add(song);
		case 6:
			song = new HashMap<String, Object>();
			song.put("id", "7tOcPDj3vyopZ404pY6UuP");
			song.put("name", "Shooting Stars");
			song.put("popularity", new Integer(47));
			song.put("preview_url", "spotify:track:7tOcPDj3vyopZ404pY6UuP");
			song.put("album_name", "Shooting Stars");
			song.put("album_image", "https://i.scdn.co/image/48ce996cb37b2e011b3374e931483c77d9fb594e");
			song.put("artists_name", "Bag Raiders");
			gems.add(song);
		case 7:
			song = new HashMap<String, Object>();
			song.put("id", "7GhIk7Il098yCjg4BQjzvb");
			song.put("name", "Never Gonna Give You Up");
			song.put("popularity", new Integer(64));
			song.put("preview_url", "spotify:track:7GhIk7Il098yCjg4BQjzvb");
			song.put("album_name", "Whenever You Need Somebody");
			song.put("album_image", "https://i.scdn.co/image/568017ab80000e71ca299909806898f75a948456");
			song.put("artists_name", "Rick Astley");
			gems.add(song);
		}
		
			
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
	
		switch (pop.size()) {
		case 0:
			HashMap<String, Object> song = new HashMap<String, Object>();
			song.put("id", "3PGdPUMdUg7a3Tgr5gkfKK");
			song.put("name", "Don't Wanna Know (feat. Kendrick Lamar)");
			song.put("popularity", new Integer(74));
			song.put("preview_url", "spotify:track:3PGdPUMdUg7a3Tgr5gkfKK");
			song.put("album_name", "Red Pill Blues (Deluxe)");
			song.put("album_image", "https://i.scdn.co/image/ef81c0753690ea5ff192ff964b66848866f363fa");
			song.put("artists_name", "Maroon 5");
			pop.add(song);
		case 1:
			song = new HashMap<String, Object>();
			song.put("id", "1jYiIOC5d6soxkJP81fxq2");
			song.put("name", "I'm the One");
			song.put("popularity", new Integer(80));
			song.put("preview_url", "spotify:track:1jYiIOC5d6soxkJP81fxq2");
			song.put("album_name", "Grateful");
			song.put("album_image", "https://i.scdn.co/image/600e240d5d48d86cf564854ba0e2b393ba4b9fdd");
			song.put("artists_name", "DJ Khaled");
			pop.add(song);
		case 2:
			song = new HashMap<String, Object>();
			song.put("id", "0ofbQMrRDsUaVKq2mGLEAb");
			song.put("name", "Havana");
			song.put("popularity", new Integer(100));
			song.put("preview_url", "spotify:track:0ofbQMrRDsUaVKq2mGLEAb");
			song.put("album_name", "Havana");
			song.put("album_image", "https://i.scdn.co/image/a3a0d4f89c07e1886452cb301bfc67f6a28366bb");
			song.put("artists_name", "Camila Cabello");
			pop.add(song);
		case 3:
			song = new HashMap<String, Object>();
			song.put("id", "1OmcAT5Y8eg5bUPv9qJT4R");
			song.put("name", "rockstar");
			song.put("popularity", new Integer(95));
			song.put("preview_url", "spotify:track:1OmcAT5Y8eg5bUPv9qJT4R");
			song.put("album_name", "rockstar");
			song.put("album_image", "https://i.scdn.co/image/bcc3516c4bcf5efb6e23abb50883bbcbe45677e5");
			song.put("artists_name", "Post Malone");
			pop.add(song);
		case 4:
			song = new HashMap<String, Object>();
			song.put("id", "34oB5r0lcN3fYWCs2uA1k5");
			song.put("name", "The Cure");
			song.put("popularity", new Integer(74));
			song.put("preview_url", "spotify:track:34oB5r0lcN3fYWCs2uA1k5");
			song.put("album_name", "The Cure");
			song.put("album_image", "https://i.scdn.co/image/795a07a4a2d6c0134527a8fea47ab37e09972db1");
			song.put("artists_name", "Lady Gaga");
			pop.add(song);
		case 5:
			song = new HashMap<String, Object>();
			song.put("id", "0wwPcA6wtMf6HUMpIRdeP7");
			song.put("name", "Hotline Bling");
			song.put("popularity", new Integer(72));
			song.put("preview_url", "spotify:track:0wwPcA6wtMf6HUMpIRdeP7");
			song.put("album_name", "Views");
			song.put("album_image", "https://i.scdn.co/image/260c2e74e67a15cf61ac72f8264cc6caec5f7a66");
			song.put("artists_name", "Drake");
			pop.add(song);
		case 6:
			song = new HashMap<String, Object>();
			song.put("id", "2y6vf5vYJFow4oABKSW1m3");
			song.put("name", "Swish Swish");
			song.put("popularity", new Integer(74));
			song.put("preview_url", "spotify:track:2y6vf5vYJFow4oABKSW1m3");
			song.put("album_name", "Witness");
			song.put("album_image", "https://i.scdn.co/image/9188e3e1842827432798582ade69685c7388b1ae");
			song.put("artists_name", "Katy Perry");
			pop.add(song);
		case 7:
			song = new HashMap<String, Object>();
			song.put("id", "5Z01UMMf7V1o0MzF86s6WJ");
			song.put("name", "Lose Yourself - Soundtrack Version");
			song.put("popularity", new Integer(77));
			song.put("preview_url", "spotify:track:5Z01UMMf7V1o0MzF86s6WJ");
			song.put("album_name", "Curtain Call (Deluxe)");
			song.put("album_image", "https://i.scdn.co/image/d8e64797fed77c9fd5dd53a41dd887b1bace8cb1");
			song.put("artists_name", "Eminem");
			pop.add(song);
		}
		
		
	}
}
