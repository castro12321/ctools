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
	private static final String STATS_TABLENAME = "ctools_stats";
	/** How many days to wait before purging players data */
	private static final int DAYS_TO_WAIT = 100;
	
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
	}
}
