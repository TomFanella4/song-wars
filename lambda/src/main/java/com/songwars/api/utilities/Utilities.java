package com.songwars.api.utilities;

import java.awt.Point;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.Md5Utils;
import com.google.gson.Gson;

public class Utilities {


    public static Connection getRemoteConnection(Context context) {
		try {
			Class.forName(System.getenv("JDBC_DRIVER"));
			String dbName = System.getenv("RDS_DB_NAME");
			String userName = System.getenv("RDS_USERNAME");
			String password = System.getenv("RDS_PASSWORD");
			String hostname = System.getenv("RDS_HOSTNAME");
			String port = System.getenv("RDS_PORT");
			String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password="
					+ password;
			context.getLogger().log("Before Connection Attempt \n" + jdbcUrl + "\n");
			Connection con = DriverManager.getConnection(jdbcUrl);
			if (con == null) {
				context.getLogger().log("Connection is null");
			}
			context.getLogger().log("Success");
			return con;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
    
    
    /**
     * makeHttpsRequest:
     * Makes a https request of any method. Allows inserting a JSON body.
     * If no Content-Type is specified in header then "x-www-urlencoded" is assumed
     * 
     * @param url_w_queryparams - url + any query parameters
     * @param method - http method in ALL CAPS.
     * @param body - JSON body to be inserted into request
     * @return - HttpsURLConnection where you can access any information about the response body or code.
     * @throws IOException - MUST PERFORM finally { connection.disconnect() } 
     */
    public static HttpsURLConnection makeHttpsRequest(String url_w_queryparams, String method, Map<String, String> headers, Map<String, Object> body) throws IOException
    {
    	URL url = new URL(url_w_queryparams);
    	HttpsURLConnection.setFollowRedirects(false);
    	HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
    	con.setRequestMethod(method);
    	con.setUseCaches(false);
    	
    	// Insert headers
    	if (headers != null) {
    		for (String key : headers.keySet()) {
    			con.setRequestProperty(key, headers.get(key));
    		}
    		if (body != null && !headers.containsKey("Content-Type"))
        		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    	}
    	else if (body != null)
    		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    	
    	// Insert Body
    	if (body != null) {
    		if (con.getRequestProperty("Content-Type").equals("application/x-www-form-urlencoded")) {
	    		StringBuilder result = new StringBuilder();
	    		boolean first = true;
	    		for (String key : body.keySet()) {
	    			if (first)
	    				first = false;
	    			else
	    				result.append("&");
	    			result.append(URLEncoder.encode(key, "UTF-8"));
	    			result.append("=");
	    			result.append(URLEncoder.encode((String) body.get(key), "UTF-8"));
	    		}
	    		
	    		con.setDoOutput(true);
	    		BufferedOutputStream out = new BufferedOutputStream(con.getOutputStream());
	    		out.write(result.toString().getBytes("UTF-8"));
	    		out.flush();
	    		out.close();
    		} else {
	    		con.setDoOutput(true);
	    		BufferedOutputStream out = new BufferedOutputStream(con.getOutputStream());
    			out.write(new Gson().toJson(body).toString().getBytes());
    		}
    	} else {
    		con.setDoOutput(false);
    	}
    	
    	return con;
    	
    }
    
    
    
    /**
	 * generateCookie - generates a unique number by multiplying the bytes of the argument 
	 * String with the current time in nanoseconds of the machine.
	 * 
	 * @param arg - input string to salt the hash. Could be the username, etc.
	 * @return - a pseudo-random integer.
	 */
	public static int generateCookie(String arg) {
    	BigInteger nameint = new BigInteger(arg.getBytes());
        BigInteger timeint = new BigInteger(Long.toString(System.nanoTime()));
        return nameint.multiply(timeint).intValue();
    }
	
	
	
	/**
	 * Checks users table in database to see if the cookie already exists.
	 * 
	 * @param cookie
	 * @param con - connection to database, already openned.
	 * @return - true if cookie is unique, false otherwise.
	 * @throws SQLException
	 */
	public static boolean isUniqueCookie(int cookie, Connection con) throws SQLException {
		
		String query = "SELECT cookie FROM users WHERE cookie=" + cookie + " LIMIT 1";
		Statement statement = con.createStatement();
		ResultSet result = statement.executeQuery(query);
		statement.close();
		
		if (result.next())
			return false;
		else
			return true;
		
	}
	
	
	/**
	 * @deprecated
	 * 
	 * Returns the position in the bracket given the round of the position.
	 * @param round - number from 1 to 4
	 * @return - position from 1 to 8/round.
	 */
	public static int generateRandomPosition(int round) {
		
		return (int) Math.ceil(Math.random() * (8/round));
				
	}
	/**
	 * Returns a random value from the list.
	 * @param list of values
	 * @return 1 value from list
	 */
	public static int getRandomIn(Integer[] list) {
		
		return list[(int) Math.ceil(Math.random() * list.length)].intValue();
		
	}
	/**
	 * Returns the opponent's position to the given position.
	 * @param position - number from 1 to 8/round
	 * @return - number from 1 to 8/round
	 */
	public static int getOpponentsPosition(int position) {
		if (position % 2 == 0)
			return position - 1;
		else
			return position + 1;
	}
	
}
