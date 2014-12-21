/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.stats;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import castro.ctools.modules.CModule;

public class Stats extends CModule implements Runnable
{
	private static HashMap<String, PlayerData> players = new HashMap<>();
	public  static StatsSQL sql;
	
	
	public Stats()
	{
		sql = new StatsSQL(plugin);
		final int second = 20;
		plugin.scheduleSyncRepeatingTask(this, 60*second, 60*second);
		plugin.registerEvents(new StatsListener());
		
		// TODO: remove it! intended to be run only once
		OfflinePlayer[] offPlayers = Bukkit.getOfflinePlayers();
		plugin.log("STATS UPDATE " + offPlayers.length);
		for(OfflinePlayer offPlayer : offPlayers)
		{
			sql.updatePlayerUUID(offPlayer.getName(), offPlayer.getUniqueId());
		}
	}
	
	
	public static PlayerData get(Player player)
	{ return get(player.getName()); }
	public static PlayerData get(String playername)
	{
		PlayerData pdata = players.get(playername);
		if(pdata == null)
		{
			pdata = sql.getPlayer(playername);
			if(pdata != null)
				players.put(playername, pdata);
		}
		return pdata;
	}
	
	
	public void run()
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			PlayerData playerdata = get(player);
			playerdata.lastWorld  = player.getWorld().getName();
			playerdata.playtime  += 1;
			playerdata.save();
		}
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		PlayerData pdata = get(player);
		if(pdata == null)
		{
			pdata = sql.getOrCreate(player);
			players.put(player.getName(), pdata);
		}
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
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		switch(command.getName())
		{
		case "searchplayerstotrack":
			if(sender.hasPermission("aliquam.admin"))
			{
    			Set<String> foundPlayers = new DataSearch().searchPlayers();
    			for(String player : foundPlayers)
    			{
    				OfflinePlayer off = Bukkit.getOfflinePlayer(player);
					PlayerData pData = sql.getOrCreate(player, off.getUniqueId(), "imported_ctools");
					if(pData != null)
					{
						if(pData.lastWorld.equals("imported_ctools"))
							plugin.log("- Added " + pData.playername);
					}
					else
					{
						plugin.log("- Cannot import stats player " + player);
						continue;
					}
    			}
			}
			break;
		case "playtime":
			String p = sender.getName();
			if(args.length > 0)
				p = args[0];
			PlayerData pdata = get(p);
			if(pdata != null)
				plugin.sendMessage(sender, "Playtime: " + get(p).playtime + " minutes");
			break;
		}
		
		return true;
	}
	
	
	@Override public boolean isListener()   { return true; }
	@Override public String[] getCommands() { return new String[] {"searchplayerstotrack", "playtime"}; }
}
