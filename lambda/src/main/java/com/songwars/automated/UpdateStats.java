package com.songwars.automated;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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
		// Database Connection:
		Connection con = Utilities.getRemoteConnection(context);
		
		try {
			// Make Insert and Delete ONE TRANSACTION:
			con.setAutoCommit(false);
			
			
			// Get second stat: # of votes counted
			String query = "SELECT votes FROM last_week_bracket";
			PreparedStatement pstatement = con.prepareStatement(query);
			ResultSet result = pstatement.executeQuery();
			con.commit();
			
			while (result.next()) {
				total_votes += result.getInt("votes");
			}

			//pstatement.close();
			

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
		
		return "Success!";
    }

}
