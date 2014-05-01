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

package castro.ctools.modules;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import castro.ctools.PermissionsBridge;
import castro.ctools.Plugin;
import castro.ctools.modules.stats.PlayerData;
import castro.ctools.modules.stats.Stats;

public class AutoRank extends CModule implements Runnable
{
	private static final String rankFrom = "guest";
	private static final String rankTo   = "player";
	private static final int    playtimeNeededMinutes = 180;
	
	public AutoRank()
	{
		final int second = 20;
		final int minute = 60*second;
		plugin.scheduleSyncRepeatingTask(this, 1*minute, 1*minute);
	}
	
	
	@Override
	public void run()
	{
		Player[] players = Bukkit.getOnlinePlayers();
		for(Player player : players)
			autoRank(player);
	}
	
	
	private void autoRank(Player player)
	{
		PlayerData pdata = Stats.get(player);
		if(pdata.playtime > playtimeNeededMinutes)
		{
			String[] groups = Plugin.permission.getPlayerGroups(player);
			for(String group : groups)
				if(group.equalsIgnoreCase("guest"))
					rankup(player);
		}
	}
	
	
	private void rankup(Player player)
	{
		if(PermissionsBridge.addToGroup(player, rankTo))
			PermissionsBridge.removeFromGroup(player, rankFrom);
	}


	@Override
	public boolean isListener()
	{
		return false;
	}


	@Override
	public String[] getCommands()
	{
		return null;
	}
}