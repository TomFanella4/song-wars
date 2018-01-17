package com.songwars.automated.analytics;

import java.sql.Connection;
import java.sql.SQLException;

public interface Hypothesis 
{
	
	public abstract String summary(); 

	public abstract int upToDate(Connection con) throws SQLException;
	
	public abstract int update(Connection con) throws SQLException;
	
}
