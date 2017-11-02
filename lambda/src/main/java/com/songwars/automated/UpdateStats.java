package com.songwars.automated;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.songwars.api.utilities.Utilities;

public class UpdateStats implements RequestHandler<Object, String> {
	
	private Context context;
	private LambdaLogger logger;
	
	
	/**
	 * The current stats to be displayed on the website are: 
	 * 
	 * # of recommendations for this week (inserted from migrate brackets!)
	 * // TODO: Add this to MigrateBrackets!
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
	

	// TODO: Implement this with attention to multiple possible brackets.
	
	@Override
	public String handleRequest(Object input, Context context) {
				
	
		// Init:
		this.context = context;
		this.logger = context.getLogger();
		// Local Variables:
		String bracket_id = null;
		int total_votes = 0;
		HashMap<String, Integer> artists = new HashMap<String, Integer>();
		ArrayList<Entry<String, Integer>> artists_list;
		HashMap<String, Integer> song_count = new HashMap<String, Integer>();
		HashMap<String, String> song_names = new HashMap<String, String>();
		ArrayList<Entry<String, Integer>> song_list;
		// Database Connection:
		Connection con = Utilities.getRemoteConnection(context);
		
		try {
			// Make Insert and Delete ONE TRANSACTION:
			con.setAutoCommit(false);
			
			
			// Get stats from current week (total votes and artists nominations)
			String query = "SELECT * FROM last_week_bracket";
			PreparedStatement pstatement = con.prepareStatement(query);
			ResultSet result = pstatement.executeQuery();
			con.commit();
			
			while (result.next()) {
				total_votes += result.getInt("votes");
				if (result.getInt("round") == 1 && !artists.containsKey(result.getString("artists_name")))
					artists.put(result.getString("artists_name"), new Integer(1));
				else if (result.getInt("round") == 1)
					artists.put(result.getString("artists_name"), new Integer(artists.get(result.getString("artists_name")).intValue() + 1));
			}
			
			result.close();
			pstatement.close();
			
			// TODO: Check if current bracket has completed winner, and add that winner to songs.
			
			// Get stats from history:
			
			// 		Get artists nominations:
			query = "SELECT * FROM bracket_history WHERE round=1";
			pstatement = con.prepareStatement(query);
			result = pstatement.executeQuery();
			con.commit();
			
			while (result.next()) {
				if (!artists.containsKey(result.getString("artists_name")))
					artists.put(result.getString("artists_name"), new Integer(1));
				else
					artists.put(result.getString("artists_name"), new Integer(artists.get(result.getString("artists_name")).intValue() + 1));
			}
			
			result.close();
			pstatement.close();
			
			// 		Get song wins:
			query = "SELECT * FROM bracket_history WHERE round=5";
			pstatement = con.prepareStatement(query);
			result = pstatement.executeQuery();
			con.commit();
			
			while (result.next()) {
				if (!song_count.containsKey(result.getString("id")))
					song_count.put(result.getString("id"), new Integer(1));
				else
					song_count.put(result.getString("id"), new Integer(song_count.get(result.getString("id")).intValue() + 1));
				
				song_names.put(result.getString("id"), result.getString("name"));
			}
			
			result.close();
			pstatement.close();
			
			logger.log("song_names: " + song_names + "\n");
			logger.log("song_count: " + song_count + "\n");
			
			
			// Compute top 10 for both lists:
			artists_list = new ArrayList<Entry<String, Integer>>(artists.entrySet());
			Collections.sort(artists_list, new Comparator<Entry<String, Integer>>() { 
				@Override
				public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
					return e2.getValue() - e1.getValue();
				}
			});
			song_list = new ArrayList<Entry<String, Integer>>(song_count.entrySet());
			Collections.sort(song_list, new Comparator<Entry<String, Integer>>() { 
				@Override
				public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
					return e2.getValue() - e1.getValue();
				}
			});
			
			logger.log("sorted: " + song_list + "\n");
			
			// Update/Insert values into stats table:
			query = "INSERT INTO stats (id, name, value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name=?, value=?";
			pstatement = con.prepareStatement(query);
			// Stat #1 - performed in MigrateBrackets
			// Stat #2
			pstatement.setInt(1, 2);
			pstatement.setString(2, "votes");
			pstatement.setInt(3, total_votes);
			pstatement.setString(4, "votes");
			pstatement.setInt(5, total_votes);
			pstatement.addBatch();
			// Stat #3-12
			for (int i = 0; i < 10 && artists_list.size() > i; i++) {
				pstatement.setInt(1, i + 3);
				pstatement.setString(2, artists_list.get(i).getKey());
				pstatement.setInt(3, artists_list.get(i).getValue());
				pstatement.setString(4, artists_list.get(i).getKey());
				pstatement.setInt(5, artists_list.get(i).getValue());
				pstatement.addBatch();
			}
			// Stat #13-22
			for (int i = 0; i < 10 && song_list.size() > i; i++) {
				logger.log("inserting " + i + "\n");
				pstatement.setInt(1, i+13);
				pstatement.setString(2, song_names.get(song_list.get(i).getKey()));
				pstatement.setInt(3, song_list.get(i).getValue());
				pstatement.setString(4, song_names.get(song_list.get(i).getKey()));
				pstatement.setInt(5, song_list.get(i).getValue());
				pstatement.addBatch();
			}
			pstatement.executeBatch();
			con.commit();
			
			pstatement.close();
		

		} catch (SQLException ex) {
			// handle any errors
			ex.printStackTrace();
			logger.log("SQLException: " + ex.getMessage());
			logger.log("SQLState: " + ex.getSQLState());
			logger.log("VendorError: " + ex.getErrorCode());

			throw new RuntimeException("[InternalServerError] - SQL error occured.");
			
		} finally {
			context.getLogger().log("Closing the connection." + "\n");
			if (con != null)
				try {
					con.close();
				} catch (SQLException ignore) {
					throw new RuntimeException("[InternalServerError] - SQL error occured and is having trouble closing connection.");
				}
		}
		
		return "Success!";
    }

}
