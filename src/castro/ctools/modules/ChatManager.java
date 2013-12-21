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

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;


public class ChatManager extends CModule
{
	public Permission permission;
	private static final int STAFF = 0;
	private static final int USER  = 1;
	private int rank = USER;
	
	
	public ChatManager()
	{
		ServicesManager services = plugin.getServer().getServicesManager();
		RegisteredServiceProvider<Permission> permissionProvider = services.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null)
			permission = permissionProvider.getProvider();
	}
	
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		String format = event.getFormat();
		format = format.replace("<", "").replace(">", "");
		format = getPrefix(player) + format;
		event.setFormat(format);
	}
	
	
	private String getPrefix(Player player)
	{
		String[] groups = permission.getPlayerGroups(player);
		String   prefix = "";
		for(String group : groups)
		{
			prefix = getPrefix(group);
			if(rank == STAFF)
				break;
		}
		
		ChatColor YELLOW = ChatColor.YELLOW;
		ChatColor WHITE  = ChatColor.WHITE;
		return YELLOW + "[" + prefix + YELLOW + "] " + WHITE;
	}
	
	
	private String getPrefix(String group)
	{	
		switch(group)
		{
		case "admins":        rank = STAFF; return ChatColor.DARK_RED     + "Admin";
		case "respected":     rank = STAFF; return ChatColor.YELLOW       + "Respected";
		case "technik":       rank = STAFF; return ChatColor.LIGHT_PURPLE + "Technik";
		case "dev":           rank = STAFF; return ChatColor.DARK_AQUA    + "dev";
		case "smod":          rank = STAFF; return ChatColor.RED          + "sMod";
		case "mod":           rank = STAFF; return ChatColor.RED          + "Mod";
		case "kmod":          rank = STAFF; return ChatColor.RED          + "kMod";
		case "helper":        rank = STAFF; return ChatColor.DARK_PURPLE  + "Assistant";
		case "friends":       rank = STAFF; return ChatColor.LIGHT_PURPLE + "Friend";
		case "headarchitect": rank = USER;  return ChatColor.GOLD         + "HeadArchitect";
		case "architect":     rank = USER;  return ChatColor.DARK_BLUE    + "Architect";
		case "designer":      rank = USER;  return ChatColor.AQUA         + "Designer";
		case "advbuilder":    rank = USER;  return ChatColor.DARK_GREEN   + "advBuilder";
		case "builder":       rank = USER;  return ChatColor.GREEN        + "Builder";
		case "familiar":      rank = USER;  return ChatColor.DARK_GRAY    + "Familiar";
		case "player":        rank = USER;  return ChatColor.GRAY         + "Player";
		case "guest":         rank = USER;  return ChatColor.WHITE        + "Guest";
		}
		return ChatColor.WHITE + group;
	}
	
	
	@Override public boolean isListener()	{ return true; }
	@Override public String[] getCommands()	{ return null; }
}