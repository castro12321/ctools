/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.groups;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import castro.ctools.EventListener;
import castro.ctools.modules.CModule;


public class ChatManager extends CModule
{
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		event.setJoinMessage(
			  ChatColor.GREEN + "+" 
			+ ChatColor.WHITE + " [" + EventListener.lastJoinedDomain + "] " 
			+ event.getPlayer().getName());
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		event.setQuitMessage(ChatColor.RED + "- " + ChatColor.WHITE + event.getPlayer().getName());
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKickEvent(PlayerKickEvent event)
	{
		event.setLeaveMessage(ChatColor.RED + "- " + ChatColor.WHITE + event.getPlayer().getName());
	}
	
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		
		if(player.hasPermission("castro.colors"))
		{
			String message = event.getMessage();
			message = ChatColor.translateAlternateColorCodes('&', message);
			event.setMessage(message);
		}
		
		Group group = GroupManager.get(player);
		
		String format = event.getFormat()
			.replace("<", group.prefix)
			.replace(">", group.suffix); // format is like <$nick> $msg so we need to remove angle braces
		
		event.setFormat(format);
	}
	
	
	@Override public boolean isListener()	{ return true; }
	@Override public String[] getCommands()	{ return null; }
}