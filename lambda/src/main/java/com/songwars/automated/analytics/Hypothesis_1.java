package com.songwars.automated.analytics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

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
		
		while (result.next()) {
			int diff = compareQualifiers(result.getString("qualifier"), maxDates.get(result.getString("group")));
			if (diff == 0)
				;
			else if (diff > 0)
				
		}
		
		pstatement.close();
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
