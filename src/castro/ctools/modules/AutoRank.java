/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import castro.ctools.PermissionsBridge;
import castro.ctools.modules.stats.PlayerData;
import castro.ctools.modules.stats.Stats;

public class AutoRank extends CModule implements Runnable
{
	private static final String rankFrom = "guest";
	private static final String rankTo   = "player";
	private static final int    playtimeNeededMinutes = 15;
	
	public AutoRank()
	{
		final int second = 20;
		final int minute = 60*second;
		plugin.scheduleSyncRepeatingTask(this, 1*minute, 1*minute);
	}
	
	
	@Override
	public void run()
	{
		for(Player player : Bukkit.getOnlinePlayers())
			autoRank(player);
	}
	
	
	private void autoRank(Player player)
	{
		PlayerData pdata = Stats.get(player);
		if(pdata.playtime > playtimeNeededMinutes)
		{
			String[] groups = permission.getPlayerGroups(player);
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