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

package castro.ctools.modules.purger.players;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import castro.ctools.modules.purger.Purger;
import castro.ctools.modules.purger.PurgerSQL;


class ModuleMultiInventories extends PlayerPurgerModule
{
	public ModuleMultiInventories(String player)
    {
		super(player);
    }
	
	@Override
	protected boolean purge()
	{
		log("- Purging MultiInv for " + player);
		
		PurgerSQL sql = Purger.purgerSQL;
		try
		{
			PreparedStatement ps = sql.getPreparedStatement("deletePlayerInvFromMultiInv");
			ps.setString(1, player);
			ps.executeUpdate();
			
			ps = sql.getPreparedStatement("deletePlayerInvFromMultiInv");
			ps.setString(1, player);
			ps.executeUpdate();
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	@Override
	protected boolean backup()
	{
		// As we are only creative server (well, you can set survival, but that doesn't matter)
		// We don't need to backup the inventory
		return true;
	}
}
