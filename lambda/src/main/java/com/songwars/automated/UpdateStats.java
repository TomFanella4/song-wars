package com.songwars.automated;

import java.sql.Connection;
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
