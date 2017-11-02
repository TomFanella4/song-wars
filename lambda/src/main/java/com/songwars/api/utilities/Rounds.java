package com.songwars.api.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Rounds {
	
	/* Rounds returns the round using the time supplied to one of these GET functions:
	 * getFromDate, getFromMillis...
	 * 
	 * Brackets start on Tuesday and end Friday midnight. Therefore, Tuesday should 
	 * return 1, Wednesday 2, Thursday 3, and Friday 4. If the day is any other day, 
	 * the round returned is -1.
	 */
	
	public static int getFromDate(String date, String format) {
		
		int round = 0;
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat(format).parse(date));
		} catch (ParseException pe) {
			throw new RuntimeException("[BadRequest] - Couldn't parse supplied date.");
		}
		
		round = c.get(Calendar.DAY_OF_WEEK) - 2;
		if (round < 1 || round > 4)
			throw new RuntimeException("[VotingClosed] - Bracket not open today.");
		return round;
	}
	
	public static int getFromMillis(long millis) {
		
		int round = 0;
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(millis);
		
		round = c.get(Calendar.DAY_OF_WEEK);
		if (round == Calendar.SUNDAY || round == Calendar.MONDAY || round == Calendar.SATURDAY)
			throw new RuntimeException("[VotingClosed] - Bracket not open today.");
		else if (round == Calendar.TUESDAY)
			return 1;
		else if (round == Calendar.WEDNESDAY)
			return 2;
		else if (round == Calendar.THURSDAY)
			return 3;
		else if (round == Calendar.FRIDAY)
			return 4;
		else
			throw new RuntimeException("[InternalServerError] - Unexpected value from Calendar.DAY_OF_WEEK.");
	}
	
	public static int getFromEnviron() {
		return Integer.parseInt(System.getenv("ROUND"));
	}

}
