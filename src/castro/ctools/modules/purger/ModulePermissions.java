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

import org.bukkit.World;

import castro.base.plugin.CUtils;
import castro.ctools.Plugin;


public class ModulePermissions extends Module
{
	boolean purge (String player)
	{
		//OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(player);
		String[] groups = Plugin.permission.getPlayerGroups((World)null, player);
		for(String group : groups)
			if(!Plugin.permission.playerRemoveGroup((World)null, player, group))
				return false;
		return true;
	}
	
	
	boolean backup(String player)
	{
		String[] groups = Plugin.permission.getPlayerGroups((World)null, player);
		return backupText("groups", player, CUtils.joinArgs(groups));
	}
}
