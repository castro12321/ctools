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

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;


class GroupSettings
{
	public final boolean staff;
	public final String  prefix;
	public final String  suffix;
	
	public GroupSettings(boolean staff, String prefix)
	{
		this.staff  = staff;
		this.prefix = prefix;
		if(staff)
			suffix = ChatColor.WHITE + ":";
		else
			suffix = ChatColor.WHITE + ":" + ChatColor.GRAY;
	}
}


public class ChatManager extends CModule
{
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		plugin.reloadWELimit(player);
		Bank.get().checkPlayerBankAccount(player);
		
		event.setJoinMessage(ChatColor.GREEN + "+ " + ChatColor.WHITE + event.getPlayer().getName());
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
		
		GroupSettings settings = getSettings(player);
		String prefix = ChatColor.YELLOW + "[" + settings.prefix + ChatColor.YELLOW + "] " + ChatColor.WHITE;
		String suffix = settings.suffix;
		String format = event.getFormat()
			.replace("<", "").replace(">", suffix); // format is like <$nick> $msg so we need to remove angle braces
		format = prefix + format;
		
		event.setFormat(format);
	}
	
	
	private GroupSettings getSettings(Player player)
	{
		String[] groups = plugin.permission.getPlayerGroups(player);
		GroupSettings settings = null;
		for(String group : groups)
		{
			settings = getSettings(group);
			if(settings.staff)
				break;
		}
		return settings;
	}
	
	
	private GroupSettings getSettings(String group)
	{	
		switch(group)
		{
		case "admins":        return new GroupSettings(true,  ChatColor.DARK_RED     + "Admin");
		case "respected":     return new GroupSettings(true,  ChatColor.YELLOW       + "Respected");
		case "technik":       return new GroupSettings(true,  ChatColor.LIGHT_PURPLE + "Technik");
		case "dev":           return new GroupSettings(true,  ChatColor.DARK_AQUA    + "dev");
		case "smod":          return new GroupSettings(true,  ChatColor.RED          + "sMod");
		case "mod":           return new GroupSettings(true,  ChatColor.RED          + "Mod");
		case "kmod":          return new GroupSettings(true,  ChatColor.RED          + "kMod");
		case "helper":        return new GroupSettings(true,  ChatColor.DARK_PURPLE  + "Assistant");
		case "friends":       return new GroupSettings(true,  ChatColor.LIGHT_PURPLE + "Friend");
		case "headarchitect": return new GroupSettings(false, ChatColor.GOLD         + "HeadArchitect");
		case "architect":     return new GroupSettings(false, ChatColor.DARK_BLUE    + "Architect");
		case "designer":      return new GroupSettings(false, ChatColor.AQUA         + "Designer");
		case "advbuilder":    return new GroupSettings(false, ChatColor.DARK_GREEN   + "advBuilder");
		case "builder":       return new GroupSettings(false, ChatColor.GREEN        + "Builder");
		case "familiar":      return new GroupSettings(false, ChatColor.DARK_GRAY    + "Familiar");
		case "player":        return new GroupSettings(false, ChatColor.GRAY         + "Player");
		case "guest":         return new GroupSettings(false, ChatColor.WHITE        + "Guest");
		default:              return new GroupSettings(false, ChatColor.WHITE        + "?");
		}
	}
	
	
	@Override public boolean isListener()	{ return true; }
	@Override public String[] getCommands()	{ return null; }
}