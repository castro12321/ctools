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

package castro.ctools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import castro.base.data.SQLBase;

public class SQL extends SQLBase 
{
	protected static Plugin plugin = Plugin.get();
	
	private final String TABLENAME = "ctools_PLAYERS";
	private final String REGZAND_MODREQ_TABLENAME = "AR_requests";
			  
	
	public SQL()
	{
		super(plugin, true);
		
		try 
		{			
			Connection conn = getConn();
			conn.createStatement().executeUpdate(
					  "CREATE TABLE IF NOT EXISTS "+TABLENAME+"("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "nick varchar(20) NOT NULL, "
					+ "count INT, " // Ile razy uzyl modreq
					+ "lastCommand INT, "
					+ "PRIMARY KEY(id), "
					+ "UNIQUE(id), "
					+ "UNIQUE(nick)"
					+ ") ENGINE=MyIsam "
					);
			
			prepareStatements();
		}
		catch (SQLException e) 
		{
			printErrors(e);
		}
	}
	
	
	private ResultSet getPlayer(String playername) throws SQLException
	{
		PreparedStatement prep = getPreparedStatement("selectPlayer");
		prep.setString(1, playername);
		return prep.executeQuery();
	}
	
	
	private void insertPlayer(String playername, long now) throws SQLException
	{
		PreparedStatement prep = getPreparedStatement("insertPlayer");
		prep.setString(1, playername);
		prep.setInt(2, 0);
		prep.setLong(3, now);
		prep.executeUpdate();
	}
	
	
	private void resetPlayer(String playername, long now) throws SQLException
	{
		PreparedStatement ps = getPreparedStatement("updatePlayer");
		ps.setInt(1, 1);
		ps.setLong(2, now);
		ps.setString(3, playername);
		ps.executeUpdate();
	}
	
	
	private void incrementPlayer(String playername) throws SQLException
	{
		PreparedStatement ps = getPreparedStatement("incrementPlayer");
		ps.setString(1, playername);
		ps.executeUpdate();
	}
	
	
	public boolean sendRankRequest(Player p)
	{
		String playername = p.getName();
		final int  day = 86400; // seconds in day
		final long now = System.currentTimeMillis() / 1000l;
		
		try
		{
			ResultSet rs = getPlayer(playername);
			if(!rs.next())
			{
				insertPlayer(playername, now);
				return sendRankRequest(p);
			}
			
			int  modreqsCount = rs.getInt("count");
			
			long diff = now - rs.getLong("lastCommand");
			if(diff > 7*day)
			{
				resetPlayer(playername, now);
				modreqsCount = 0;
			}
			
			if(modreqsCount < 2)
			{
				incrementPlayer(playername);
				plugin.sendMessage(p, "You have successfully sent your " + (modreqsCount+1) + " modreq this week. "
					+ "Remember that you can send only 2 modreqs per week!");
				return true;
			}
			else
			{
				float daysLeft = (7*day - (float)diff) / ((float)day);
				plugin.sendMessage(p, "You have already sent 2 modreqs this week. "
					+ "Please wait " + daysLeft + " days to refresh the limit.");
				return false;
			}
		}
		catch(SQLException e) { printErrors(e); }
		return true;
	}
	
	
	public boolean modreqPending(Player player)
	{
		boolean pending = false;
		PreparedStatement ps = getPreparedStatement("modreqPending");
		try
		{
			ps.setString(1, player.getName());
			ResultSet rs = ps.executeQuery();
			pending = rs.next();
			rs.close();
		}
		catch(SQLException e)
		{
			printErrors(e);
		}
		return pending;
	}
	
	
	public void prepareStatements()
	{
		addStatementSQL("updatePlayer",
				  "UPDATE "+TABLENAME+" SET "
				+ "count=?, lastCommand=?"
				+ " WHERE nick=?");
		
		addStatementSQL("selectPlayer",
				    "SELECT * FROM " + TABLENAME
				  + " WHERE nick=?");
		
		addStatementSQL("insertPlayer",
				  "INSERT INTO "+TABLENAME+"(nick, count, lastCommand)"
				+ "VALUES(?, ?, ?)");
		
		addStatementSQL("incrementPlayer",
				  "UPDATE "+TABLENAME+" SET "
				+ "count=count+1 "
				+ "WHERE nick=?");
		
		addStatementSQL("modreqPending",
				  "SELECT * FROM "+REGZAND_MODREQ_TABLENAME
				+ " WHERE sender=? AND done=0");
	}
}
