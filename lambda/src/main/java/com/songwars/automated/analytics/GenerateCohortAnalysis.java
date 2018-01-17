package com.songwars.automated.analytics;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.songwars.api.utilities.Utilities;

public class GenerateCohortAnalysis implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private Context context;
	private LambdaLogger logger;
	
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
				
		this.context = context;
		this.logger = context.getLogger();
		// Response JSON:
		Map<String, Object> response = new HashMap<String, Object>();
		// Database Connection:
		Connection con = Utilities.getRemoteConnection(context);
				
		// TODO:
		//
		//	1. Determine what stage current cohort analysis is at.
		//	2. Decide what other analysis is left to perform.
		//	3. Perform remaining analysis on current 5 hypotheses.
		//	4. Update statistics on miscelaneous, non-cohort related objectives.
		
		ArrayList<Hypothesis> hypotheses = new ArrayList<Hypothesis>();
		hypotheses.add(new Hypothesis_1());
		//hypotheses.add(new Hypothesis_2());
		//hypotheses.add(new Hypothesis_3());
		//hypotheses.add(new Hypothesis_4());
		
		
		try {
			// 1-3. Update hypotheses if they are not up-to-date:
			for (Hypothesis h : hypotheses)
				if (h.upToDate(con) == 0)
					if (h.update(con) == 0)
						throw new RuntimeException("[InternalServerError] Error processing update to:" + h.summary());
					
			
			// 4. Update miscelaneous objectives: 
			/*String query = "SELECT * FROM user_analysis";
			Statement statement = con.createStatement();
			statement.addBatch(query);
			statement.executeBatch();
			statement.close();*/

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