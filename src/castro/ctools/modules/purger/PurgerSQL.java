/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.purger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

import castro.base.data.SQLBase;
import castro.ctools.Plugin;

public class PurgerSQL extends SQLBase
{
	/** Table name used by cTools to store players stats */
	private static final String STATS_TABLENAME   = "ctools_stats";
	/** Table name used by MultiInv to store players inventory */
	private static final String MULTI_INV_PLAYERS = "multiinv_multiinv";
	/** Table name used by MultiInv to store players ender chest */
	private static final String MULTI_INV_CHESTS  = "multiinv_enderchestinv";
	/** Table name used by MultiInv to store players ender chest */
	private static final String PEX_PERMISSIONS  = "permissions";
	/** Table name used by MultiInv to store players ender chest */
	private static final String PEX_ENTITIES  = "permissions_entity";
	/** Table name used by MultiInv to store players ender chest */
	private static final String PEX_INHERITANCE  = "permissions_inheritance";
	/** How many days to wait before purging players data */
	private static final int DAYS_TO_WAIT = 180;
	
	public PurgerSQL(Plugin plugin)
	{
		super(plugin, true);
		prepareStatements();
	}
	
	
	/**
	 * @return List of players that were offline for DAYS_TO_WAIT days.
	 */
	public Queue<String> getPlayersToBurn()
	{
		Queue<String> players = new LinkedList<>();
		
		try
        {
            PreparedStatement ps = getPreparedStatement("selectPlayersToBurn");
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            	players.add(rs.getString("nick"));
            rs.close();
        }
        catch(SQLException e) { e.printStackTrace(); }
		
		return players;
	}
	
	
	private void prepareStatements()
	{
		addStatementSQL("selectPlayersToBurn",
			  "SELECT nick FROM " + STATS_TABLENAME
			+ " WHERE seen <= CURRENT_DATE - INTERVAL "+DAYS_TO_WAIT+" DAY" 
				);
		
		addStatementSQL("deletePlayerInvFromMultiInv",
				  "DELETE FROM " + MULTI_INV_PLAYERS
				+ " WHERE inv_player = ?"
				);
		
		addStatementSQL("deletePlayerInvFromMultiInv",
				  "DELETE FROM " + MULTI_INV_CHESTS
				+ " WHERE chest_player = ?"
				);
		
		addStatementSQL("deletePermissionsFromPEX",
				  "DELETE FROM " + PEX_PERMISSIONS
				+ " WHERE name = ? AND type=1"
				);
		
		addStatementSQL("deleteEntityFromPEX",
				  "DELETE FROM " + PEX_ENTITIES
				+ " WHERE name = ? AND type=1"
				);
		
		addStatementSQL("deleteInheritanceFromPEX",
				  "DELETE FROM " + PEX_INHERITANCE
				+ " WHERE child = ? AND type=1"
				);
	}
}
