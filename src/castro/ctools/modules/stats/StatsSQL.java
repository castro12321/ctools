/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package castro.ctools.modules.stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

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
	{ return getOrCreate(player.getName(), player.getWorld().getName()); }
	public PlayerData getOrCreate(String player, String lastworld)
	{
		PlayerData playerdata = getPlayer(player);
		if(playerdata == null)
			playerdata = insertPlayer(player, lastworld);
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
				Timestamp seen         = rs.getTimestamp("seen");
				int       playtime     = rs.getInt("playtime");
				long      modreqsReset = rs.getLong("modreqsReset");
				int       modreqsCount = rs.getInt("modreqsCount");
				return new PlayerData(playername, lastWorld, seen, playtime, modreqsReset, modreqsCount);
			}
		}
		catch(SQLException e) { e.printStackTrace(); }
		
		return null;
	}
	
	
	private PlayerData insertPlayer(String player, String lastworld)
	{
		try
		{
			PreparedStatement prep = getPreparedStatement("insertPlayer");
			prep.setString(1, player);
			prep.setString(2, lastworld);
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
			ps.setString(1, playerdata.lastWorld);
			ps.setInt   (2, playerdata.playtime);
			ps.setLong  (3, playerdata.modreqsReset);
			ps.setInt   (4, playerdata.modreqsCount);
			ps.setString(5, playerdata.playername);
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
				+ " SET lastworld=?, playtime=?, modreqsReset=?, modreqsCount=?"
				+ " WHERE nick=?");
		
		addStatementSQL("selectPlayer",
				    "SELECT * FROM " + TABLENAME
				  + " WHERE nick=?");
		
		addStatementSQL("insertPlayer",
				  "INSERT INTO "+TABLENAME+"(nick, lastworld)"
				+ " VALUES(?, ?)");
		
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
