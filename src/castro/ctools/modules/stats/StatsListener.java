/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.stats;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class StatsListener implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
	{
		Player player    = event.getPlayer();
		PlayerData pdata = Stats.get(player);
		pdata.uuid       = player.getUniqueId();
		pdata.lastWorld  = player.getWorld().getName();
		pdata.save();
	}
}