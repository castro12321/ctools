/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import org.bukkit.entity.Player;

import castro.base.data.SQLBase;
import castro.ctools.Plugin;

public class StatsSQL extends SQLBase
{
	private static final String TABLENAME = "ctools_stats";
	/** Table name used by MultiInv to store players ender chest */
	private static final String PEX_ENTITIES  = "permissions_entity";
	/** Table name used by MultiInv to store players ender chest */
	private static final String PEX_INHERITANCE  = "permissions_inheritance";
	
	
	public StatsSQL(Plugin plugin)
	{
		super(plugin, true);
		prepareStatements();
		
		try
		{
			Connection conn = getConn();
			conn.createStatement().executeUpdate(
		        "CREATE TABLE IF NOT EXISTS " + TABLENAME + "("
		                + "id           INT         NOT NULL AUTO_INCREMENT, "
		                + "nick         VARCHAR(32) NOT NULL, "
		                + "uuid         VARCHAR(128), "
		                + "seen         TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
		                + "lastworld    VARCHAR(32) NOT NULL, "
		                + "playtime     INT         DEFAULT 0, " // in minutes
		                + "modreqsReset INT         DEFAULT 0, " // in seconds
		                + "modreqsCount INT         DEFAULT 0, "
		                + "PRIMARY KEY(id), "
		                + "UNIQUE(nick)"
		                + ") ENGINE=MyIsam "
		        );
		}
		catch(SQLException e) { printErrors(e); }
	}
	
	
	public PlayerData getOrCreate(Player player)
	{ return getOrCreate(player.getName(), player.getUniqueId(), player.getWorld().getName()); }
	public PlayerData getOrCreate(String player, UUID uuid, String lastworld)
	{
		PlayerData playerdata = getPlayer(player);
		if(playerdata == null)
			playerdata = insertPlayer(player, uuid, lastworld);
		return playerdata;
	}
	
	
	public PlayerData getPlayer(String playername)
	{
		try
		{
			PreparedStatement prep = getPreparedStatement("selectPlayer");
			prep.setString(1, playername);
			ResultSet rs = prep.executeQuery();
			
			if(rs.next())
			{
				String    lastWorld    = rs.getString("lastworld");
				String    suuid        = rs.getString("uuid");
				UUID      uuid         = suuid == null ? null : UUID.fromString(rs.getString("uuid"));
				Timestamp seen         = rs.getTimestamp("seen");
				int       playtime     = rs.getInt("playtime");
				long      modreqsReset = rs.getLong("modreqsReset");
				int       modreqsCount = rs.getInt("modreqsCount");
				return new PlayerData(playername, uuid, lastWorld, seen, playtime, modreqsReset, modreqsCount);
			}
		}
		catch(SQLException e) { e.printStackTrace(); }
		
		return null;
	}
	
	
	public PlayerData getPlayerByUUID(UUID uuid)
	{
		try
		{
			PreparedStatement prep = getPreparedStatement("selectPlayerByUUID");
			prep.setString(1, uuid.toString());
			ResultSet rs = prep.executeQuery();
			
			if(rs.next())
			{
				String    playername   = rs.getString("nick");
				String    lastWorld    = rs.getString("lastworld");
				Timestamp seen         = rs.getTimestamp("seen");
				int       playtime     = rs.getInt("playtime");
				long      modreqsReset = rs.getLong("modreqsReset");
				int       modreqsCount = rs.getInt("modreqsCount");
				return new PlayerData(playername, uuid, lastWorld, seen, playtime, modreqsReset, modreqsCount);
			}
		}
		catch(SQLException e) { e.printStackTrace(); }
		
		return null;
	}
	
	
	private PlayerData insertPlayer(String player, UUID uuid, String lastworld)
	{
		try
		{
			PreparedStatement prep = getPreparedStatement("insertPlayer");
			prep.setString(1, player);
			prep.setString(2, uuid.toString());
			prep.setString(3, lastworld);
			prep.executeUpdate();
		}
		catch(SQLException e) { e.printStackTrace(); }
		return getPlayer(player);
	}
	
	
	public void updatePlayer(PlayerData playerdata)
	{
		try
		{
			PreparedStatement ps = getPreparedStatement("updatePlayer");
			ps.setString(1, playerdata.uuid.toString());
			ps.setString(2, playerdata.lastWorld);
			ps.setInt   (3, playerdata.playtime);
			ps.setLong  (4, playerdata.modreqsReset);
			ps.setInt   (5, playerdata.modreqsCount);
			ps.setString(6, playerdata.playername);
			ps.executeUpdate();
		}
		catch(SQLException e) { e.printStackTrace(); }
	}
	
	public void updatePlayerUUID(String player, UUID newUUID)
	{
		try
		{
			PreparedStatement ps = getPreparedStatement("updatePlayerUUID");
			ps.setString(1, newUUID.toString());
			ps.setString(2, player);
			ps.executeUpdate();
		}
		catch(SQLException e) { e.printStackTrace(); }
	}
	
	
	public void deletePlayer(String playername) throws SQLException
	{
		PreparedStatement ps = getPreparedStatement("deletePlayer");
		ps.setString(1, playername);
		ps.executeUpdate();
	}
	
	
	private void prepareStatements()
	{
		addStatementSQL("updatePlayer",
				  "UPDATE "+TABLENAME
				+ " SET uuid=?, lastworld=?, playtime=?, modreqsReset=?, modreqsCount=?"
				+ " WHERE nick=?");
		
		addStatementSQL("updatePlayerUUID",
				  "UPDATE "+TABLENAME
				+ " SET uuid=?"
				+ " WHERE nick=?");
		
		addStatementSQL("selectPlayer",
			    "SELECT * FROM " + TABLENAME
			  + " WHERE nick=?");
		
		addStatementSQL("selectPlayerByUUID",
			    "SELECT * FROM " + TABLENAME
			  + " WHERE uuid=?");
		
		addStatementSQL("insertPlayer",
				  "INSERT INTO "+TABLENAME+"(nick, uuid, lastworld)"
				+ " VALUES(?, ?, ?)");
		
		addStatementSQL("deletePlayer",
				  "DELETE FROM " + TABLENAME +
				  " WHERE nick=?");
		
		addStatementSQL("pexEntities",
			    "SELECT name FROM " + PEX_ENTITIES
			  + " WHERE type=1");
		
		addStatementSQL("pexInheritance",
			    "SELECT child FROM " + PEX_INHERITANCE
			  + " WHERE type=1");
	}
}
