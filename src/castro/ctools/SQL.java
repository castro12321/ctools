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
	
	
	public void prepareStatements()
	{
		addStatementSQL("modreqPending",
				  "SELECT * FROM "+REGZAND_MODREQ_TABLENAME
				+ " WHERE sender=? AND done=0");
	}
}
