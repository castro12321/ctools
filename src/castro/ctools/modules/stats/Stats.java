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

package castro.ctools.modules.stats;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import castro.ctools.Plugin;
import castro.ctools.modules.CModule;

public class Stats extends CModule implements Runnable
{
	private static HashMap<String, PlayerData> players = new HashMap<>();
	private final StatsSQL statsSQL;
	
	
	public Stats(Plugin plugin)
	{
		statsSQL = new StatsSQL(plugin);
		final int second = 20;
		plugin.scheduleSyncRepeatingTask(this, 300*second, 300*second);
	}
	
	
	public static PlayerData get(Player player)
	{
		return players.get(player.getName());
	}
	
	
	public static PlayerData get(String playername)
	{
		return players.get(playername);
	}
	
	
	public void run()
	{
		Player[] players = Bukkit.getOnlinePlayers();
		for(Player player : players)
		{
			PlayerData playerdata = statsSQL.getOrCreate(player);
			playerdata.lastWorld  = player.getWorld().getName();
			playerdata.playtime  += 5;
		}
	}
	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		PlayerData playerdata = statsSQL.getOrCreate(player);
		players.put(player.getName(), playerdata);
	}
	
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		players.remove(event.getPlayer().getName());
	}
	
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event)
	{
		players.remove(event.getPlayer().getName());
	}
	
	
	@Override public boolean isListener()   { return true; }
	@Override public String[] getCommands() { return null; }
}
