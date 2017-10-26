package com.songwars.api.utilities;

import java.util.HashMap;
import java.util.Map;

public class Matchup {

	private int round;
	private int pos1;
	private int pos2;
	
	private String id1;
	private String name1;
	private int popularity1;
	private String preview_url1;
	private String album_name1;
	private String album_image1;
	private String artists_name1;
	private String bracket_id1;
	private int votes1;
	
	private String id2;
	private String name2;
	private int popularity2;
	private String preview_url2;
	private String album_name2;
	private String album_image2;
	private String artists_name2;
	private String bracket_id2;
	private int votes2;
	
	public Matchup(int round, int pos1) {
		this.round = round;
		this.pos1 = pos1;
		this.pos2 = Utilities.getOpponentsPosition(pos1);
	}

	public boolean contains(int pos) {
		if (pos1 == pos || pos2 == pos)
			return true;
		else
			return false;
	}

	public int getRound() {
		return round;
	}

	public int getPos1() {
		return pos1;
	}

	public int getPos2() {
		return pos2;
	}
	
	
	public Map<String, Object> toMap() {
		HashMap<String, Object> matchup = new HashMap<String, Object>();
		HashMap<String, Object> song1 = new HashMap<String, Object>();
		HashMap<String, Object> song2 = new HashMap<String, Object>();
		
		song1.put("position", new Integer(pos1));
		song1.put("id", id1);
		song1.put("name", name1);
		song1.put("popularity", new Integer(popularity1));
		song1.put("preview_url", preview_url1);
		song1.put("album_name", album_name1);
		song1.put("album_image", album_image1);
		song1.put("artists_name", artists_name1);
		
		song2.put("position", new Integer(pos2));
		song2.put("id", id2);
		song2.put("name", name2);
		song2.put("popularity", new Integer(popularity2));
		song2.put("preview_url", preview_url2);
		song2.put("album_name", album_name2);
		song2.put("album_image", album_image2);
		song2.put("artists_name", artists_name2);
		
		matchup.put("song1", song1);
		matchup.put("song2", song2);
		
		return matchup;
	}
	
	/*
	 * Returns map of data for song that wins in votes, and if a tie, 
	 * then whichever has lower popularity, otherwise song2 wins by default.
	 */
	public Map<String, Object> getWinner() {
		Map<String, Object> match = this.toMap();
		
		if (votes1 > votes2)
			return (Map<String, Object>) match.get("song1");
		else if (votes2 > votes1)
			return (Map<String, Object>) match.get("song2");
		else {
			if (popularity1 < popularity2)
				return (Map<String, Object>) match.get("song1");
			else	// Yes I know this picks song2 in case of complete tie... This is my random choice.
				return (Map<String, Object>) match.get("song2");
		}
	}

	
	
	public void setId(int pos, String id)
	{
		if (pos1 == pos)
			id1 = id;
		else
			id2 = id;
	}
	public void setName(int pos, String name)
	{
		if (pos1 == pos)
			name1 = name;
		else
			name2 = name;
	}
	public void setPopularity(int pos, int popularity)
	{
		if (pos1 == pos)
			popularity1 = popularity;
		else
			popularity2 = popularity;
	}
	public void setPreview_Url(int pos, String preview_url)
	{
		if (pos1 == pos)
			preview_url1 = preview_url;
		else
			preview_url2 = preview_url;
	}
	public void setAlbum_name(int pos, String album_name)
	{
		if (pos1 == pos)
			album_name1 = album_name;
		else
			album_name2 = album_name;
	}
	public void setAlbum_image(int pos, String album_image)
	{
		if (pos1 == pos)
			album_image1 = album_image;
		else
			album_image2 = album_image;
	}
	public void setArtists_name(int pos, String artists_name)
	{
		if (pos1 == pos)
			artists_name1 = artists_name;
		else
			artists_name2 = artists_name;
	}
	public void setBracket_Id(int pos, String bracket_id)
	{
		if (pos1 == pos)
			bracket_id1 = bracket_id;
		else
			bracket_id2 = bracket_id;
	}
	public void setVotes(int pos, int votes)
	{
		if (pos1 == pos)
			votes1 = votes;
		else 
			votes2 = votes;
	}
	
	
	
	public String getId1() {
		return id1;
	}

	public String getName1() {
		return name1;
	}

	public int getPopularity1() {
		return popularity1;
	}

	public String getPreview_url1() {
		return preview_url1;
	}

	public String getAlbum_name1() {
		return album_name1;
	}

	public String getAlbum_image1() {
		return album_image1;
	}

	public String getArtists_name1() {
		return artists_name1;
	}

	public String getBracket_id1() {
		return bracket_id1;
	}

	public int getVotes1() {
		return votes1;
	}

	public String getId2() {
		return id2;
	}

	public String getName2() {
		return name2;
	}

	public int getPopularity2() {
		return popularity2;
	}

	public String getPreview_url2() {
		return preview_url2;
	}

	public String getAlbum_name2() {
		return album_name2;
	}

	public String getAlbum_image2() {
		return album_image2;
	}
	
	public String getArtists_name2() {
		return artists_name2;
	}

	public String getBracket_id2() {
		return bracket_id2;
	}
	
	public int getVotes2() {
		return votes2;
	}

	

}
