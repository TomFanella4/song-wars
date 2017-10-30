package com.songwars.automated;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.songwars.api.utilities.Matchup;
import com.songwars.api.utilities.Utilities;

public class MigrateRounds implements RequestHandler<Object, String> {

	private Context context;
	private LambdaLogger logger;
	
	
	@Override
	public String handleRequest(Object input, Context context) {
				
		// Init:
		this.context = context;
		this.logger = context.getLogger();
		// Local Variables:
		String bracket_id = null;
		int round = 1;
		HashMap<String, ArrayList<Matchup>> brackets_matchups = new HashMap<String, ArrayList<Matchup>>();
		HashMap<String, ArrayList<Matchup>> brackets_next_rounds = new HashMap<String, ArrayList<Matchup>>();
		ArrayList<String> bracket_ids = new ArrayList<String>();
		// Database Connection:
		Connection con = Utilities.getRemoteConnection(context);
		
		try {
			// Make Insert and Delete ONE TRANSACTION:
			con.setAutoCommit(false);
			
			
			// Get most recent round from last_week_bracket for each bracket_id
			String query = "SELECT * FROM last_week_bracket WHERE (bracket_id, round) IN ( SELECT bracket_id, MAX(round) FROM last_week_bracket )";
			PreparedStatement pstatement = con.prepareStatement(query);
			ResultSet result = pstatement.executeQuery();
			con.commit();
			
			// Go through all current brackets:
			while (result.next()) {
				boolean exists = false;
				ArrayList<Matchup> matchups = null;
				// Create new ArrayList for bracket if not created already:
				bracket_id = result.getString("bracket_id");
				round = result.getInt("round");
				if (brackets_matchups.containsKey(bracket_id)) {
					matchups = brackets_matchups.get(bracket_id);
				} else {
					matchups = new ArrayList<Matchup>();
					brackets_matchups.put(bracket_id, matchups);
					bracket_ids.add(bracket_id);
				}
				
				// Find or create matchup for position for bracket selected, and fill with song data.
				for (int i = 0; i < matchups.size(); i++) {
					if (matchups.get(i).contains(result.getInt("position"))) {
						// Load matchup with song data
						Matchup m = matchups.get(i);
						int pos = result.getInt("position");
						
						m.setId(pos, result.getString("id"));
						m.setName(pos, result.getString("name"));
						m.setPopularity(pos, result.getInt("popularity"));
						m.setPreview_Url(pos, result.getString("preview_url"));
						m.setAlbum_name(pos, result.getString("album_name"));
						m.setAlbum_image(pos, result.getString("album_image"));
						m.setArtists_name(pos, result.getString("artists_name"));
						m.setBracket_Id(pos, result.getString("bracket_id"));
						m.setVotes(pos, result.getInt("votes"));
						
						matchups.set(i, m);
						
						exists = true;
						break;
					}
				}
				if (!exists) {		// Create new Matchup for position
					Matchup m = new Matchup(result.getInt("round"), result.getInt("position"));
					int pos = result.getInt("position");
					
					m.setId(pos, result.getString("id"));
					m.setName(pos, result.getString("name"));
					m.setPopularity(pos, result.getInt("popularity"));
					m.setPreview_Url(pos, result.getString("preview_url"));
					m.setAlbum_name(pos, result.getString("album_name"));
					m.setAlbum_image(pos, result.getString("album_image"));
					m.setArtists_name(pos, result.getString("artists_name"));
					m.setBracket_Id(pos, result.getString("bracket_id"));
					m.setVotes(pos, result.getInt("votes"));
					
					matchups.add(m);
				}
			}
			result.close();
			pstatement.close();
			
			
			// Once Matchups have been loaded, determine winners and load those into new Matchups.
			for (String id : bracket_ids) {
				ArrayList<Matchup> bracket = brackets_matchups.get(id);
				
				// Sort bracket by positions:
				Collections.sort(bracket, new Comparator<Matchup>() { 
					@Override
					public int compare(Matchup m1, Matchup m2) {
						return m1.getPos1() - m2.getPos1();
					}
				});
				
				// Iterate through pairs of matchups and move winners to one new matchup.
				ArrayList<Matchup> next_rounds = new ArrayList<Matchup>();
				for (int i = 0; i < bracket.size(); i+=2) {
					Map<String, Object> winner1 = bracket.get(i).getWinner();
					Map<String, Object> winner2 = bracket.get(i+1).getWinner();
					
					int new_pos1 = i + 1;
					int new_pos2 = Utilities.getOpponentsPosition(new_pos1);
					
					Matchup next_match = new Matchup(round + 1, new_pos1);
					
					next_match.setId(new_pos1, (String) winner1.get("id"));
					next_match.setName(new_pos1, (String) winner1.get("name"));
					next_match.setPopularity(new_pos1, ((Integer) winner1.get("popularity")).intValue());
					next_match.setPreview_Url(new_pos1, (String) winner1.get("preview_url"));
					next_match.setAlbum_name(new_pos1, (String) winner1.get("album_name"));
					next_match.setAlbum_image(new_pos1, (String) winner1.get("album_image"));
					next_match.setArtists_name(new_pos1, (String) winner1.get("artists_name"));
					next_match.setBracket_Id(new_pos1, (String) winner1.get("bracket_id"));
					
					next_match.setId(new_pos2, (String) winner2.get("id"));
					next_match.setName(new_pos2, (String) winner2.get("name"));
					next_match.setPopularity(new_pos2, ((Integer) winner2.get("popularity")).intValue());
					next_match.setPreview_Url(new_pos2, (String) winner2.get("preview_url"));
					next_match.setAlbum_name(new_pos2, (String) winner2.get("album_name"));
					next_match.setAlbum_image(new_pos2, (String) winner2.get("album_image"));
					next_match.setArtists_name(new_pos2, (String) winner2.get("artists_name"));
					next_match.setBracket_Id(new_pos2, (String) winner2.get("bracket_id"));
					
					next_rounds.add(next_match);
				}
				
				// Add list of new matchups to that bracket's 'next rounds'
				brackets_next_rounds.put(id, next_rounds);
			
			}
			
			
			logger.log("Starting INSERT QUERY: \n\n");
			// Insert all winning songs to last_week_bracket:
			query = "INSERT INTO last_week_bracket (id, name, popularity, preview_url, album_name, album_image, artists_name, votes, bracket_id, round, position)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, 0, ?, ?, ?)";
			pstatement = con.prepareStatement(query);
			for (ArrayList<Matchup> bracket : brackets_next_rounds.values()) {
				for (Matchup m : bracket) {
					
					logger.log(m.toMap().toString() + "\n");
					
					pstatement.setString(1, m.getId1());
					pstatement.setString(2, m.getName1());
					pstatement.setInt(3, m.getPopularity1());
					pstatement.setString(4, m.getPreview_url1());
					pstatement.setString(5, m.getAlbum_name1());
					pstatement.setString(6, m.getAlbum_image1());
					pstatement.setString(7, m.getArtists_name1());
					pstatement.setString(8, m.getBracket_id1());
					pstatement.setInt(9, m.getRound());
					pstatement.setInt(10, m.getPos1());
					pstatement.addBatch();

					pstatement.setString(1, m.getId2());
					pstatement.setString(2, m.getName2());
					pstatement.setInt(3, m.getPopularity2());
					pstatement.setString(4, m.getPreview_url2());
					pstatement.setString(5, m.getAlbum_name2());
					pstatement.setString(6, m.getAlbum_image2());
					pstatement.setString(7, m.getArtists_name2());
					pstatement.setString(8, m.getBracket_id2());
					pstatement.setInt(9, m.getRound());
					pstatement.setInt(10, m.getPos2());
					pstatement.addBatch();
				}
			}
			logger.log("Attempting Execution: \n\n");
			int[] statuses = pstatement.executeBatch();
			
			// Execute whole transaction:
			con.commit();
			
			// Check for SQL Status of insertions:
			for (int s : statuses)
				if (statuses[s] == PreparedStatement.EXECUTE_FAILED)
					throw new RuntimeException("[InternalServerError] - Batch inserting new round songs Failed!");
			
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

}
