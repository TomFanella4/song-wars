package com.songwars.automated.analytics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.joda.time.Instant;

public class Hypothesis_1 implements Hypothesis {

	private String lastMessage = null;
	
	@Override
	public String summary() {
		return " | Hypothesis_1: " + lastMessage + " | ";
	}
	
	@Override
	public int upToDate(Connection con) throws SQLException {
		String query = "SELECT * FROM cohort_analysis WHERE hypothesis=1";
		PreparedStatement pstatement = con.prepareStatement(query);
		ResultSet result = pstatement.executeQuery();
		
		HashMap<String, String> maxDates = new HashMap<String, String>();
		
		// Iterate through all results for hypothesis 1 and find most up to date data and see if it needs to be updated.
		while (result.next()) {
			int diff = compareQualifiers(result.getString("qualifier"), maxDates.get(result.getString("group")));
			if (diff == 0)
				throw new RuntimeException("[InternalServerError] group=" + result.getString("group") + " qualifier=" + result.getString("qualifier") + " has duplicate results.");
			else if (diff > 0)
				maxDates.put(result.getString("group"), result.getString("qualifier"));
			else if (diff < 0)
				; // Max is already set.
		}
		
		result.close();
		pstatement.close();
		
		// Checks max dates of calculated hypothesis stats with current date.
		String current = new Instant(System.currentTimeMillis()).;
		
		return 0;
	}

	@Override
	public int update(Connection con) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	/**
	 * compareQualifiers - compares qualifiers and returns which one is larger/later
	 * @param q1 - month qualifier for H1.
	 * @param q2 - month qualifier for H1.
	 * @return - >0 if q1 is greater than q2, 0 if they are equal, <0 if q1 is less than q2.
	 */
	public int compareQualifiers(String q1, String q2) {
		 return Integer.parseInt(q1) - Integer.parseInt(q2); 
	}

}
