/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import castro.base.data.SQLBase;

public class SQL extends SQLBase 
{
	protected static Plugin plugin = Plugin.get();
	private final String REGZAND_MODREQ_TABLENAME = "AR_requests";
	
	
	public SQL()
	{
		super(plugin, true);
		prepareStatements();
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
	
	public String modreqWorld(int modreqId)
	{
		String world = null;
		PreparedStatement ps = getPreparedStatement("modreqWorld");
		try
		{
			ps.setInt(1, modreqId);
			ResultSet rs = ps.executeQuery();
			if(rs.next())
				world = rs.getString("world");
			rs.close();
		}
		catch(SQLException e)
		{
			printErrors(e);
		}
		return world;
	}
	
	public void prepareStatements()
	{
		addStatementSQL("modreqPending",
				  "SELECT * FROM "+REGZAND_MODREQ_TABLENAME
				+ " WHERE sender=? AND done=0");
		addStatementSQL("modreqWorld", 
				  "SELECT world FROM "+REGZAND_MODREQ_TABLENAME
				+ " WHERE id=?");
	}
}
