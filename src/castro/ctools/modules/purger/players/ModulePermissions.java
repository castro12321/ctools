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

import org.bukkit.World;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import castro.base.plugin.CUtils;
import castro.ctools.Plugin;
import castro.ctools.modules.purger.Purger;


class ModulePermissions extends PlayerPurgerModule
{
	public ModulePermissions(String player)
    {
		super(player);
    }
	
	@Override
	protected boolean purge()
	{
		//OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(player);
		String[] groups = Plugin.permission.getPlayerGroups((World)null, player);
		for(String group : groups)
			if(!Plugin.permission.playerRemoveGroup((World)null, player, group))
				return !log("- Cannot remove from " + group + " group");
		
		// Delete from PEX
		PermissionManager pex  = PermissionsEx.getPermissionManager();
		PermissionUser pexUser = pex.getUser(player);
		pexUser.remove();
		
		// Delete leftovers if any
		try
		{
			PreparedStatement ps = Purger.purgerSQL.getPreparedStatement("deleteEntityFromPEX");
			ps.setString(1, player);
			ps.executeUpdate();
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
			return !log("- Cannot delete entity from PEX");
		}
		
		try
		{
			PreparedStatement ps = Purger.purgerSQL.getPreparedStatement("deleteInheritanceFromPEX");
			ps.setString(1, player);
			ps.executeUpdate();
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
			return !log("- Cannot delete inheritance from PEX");
		}
		
		
		return true;
	}
	
	@Override
	protected boolean backup()
	{
		String[] groups = Plugin.permission.getPlayerGroups((World)null, player);
		return backup.text("groups", player, CUtils.joinArgs(groups));
	}
}
