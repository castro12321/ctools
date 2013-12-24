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

package castro.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



public class Who extends BaseCommand
{
	List<Player> onlinePlayers;
	
	
	class Group
	{
		final String displayname;
		final String[] groupsAffected;
		List<Player> players = new ArrayList<>();
		
		Group(String groupDisplayname, String... groupsAffected)
		{
			this.displayname = groupDisplayname;
			this.groupsAffected   = groupsAffected;
			
			for(Player player : onlinePlayers)
			{
				String[] groups   = plugin.permission.getPlayerGroups(player);
				if(isInGroup(groups, groupsAffected))
					players.add(player);
			}
			
			onlinePlayers.removeAll(players);
		}
		
		
		private boolean isInGroup(String[] groups, String... searched)
		{
			for(String group : groups)
				for(String searchedGroup : searched)
					if(searchedGroup.equals(group))
						return true;
			return false;
		}
	}
	
	
	@Override
	protected boolean prep()
	{		
		return true;
	}
	
	
	@Override
	protected boolean exec()
	{
		Server server = plugin.getServer();
		List<Player> onlinePlayersFixed = Arrays.asList(server.getOnlinePlayers());
		onlinePlayers = new ArrayList<>(onlinePlayersFixed);
		plugin.sendMessage(sender, ChatColor.GOLD + "There are " + onlinePlayers.size() + " out of maximum " + server.getMaxPlayers() + " players on the server", false);
		
		List<Group> groupedPlayers = new ArrayList<>();
		groupedPlayers.add(new Group(ChatColor.DARK_RED     + "Administracja", "admins"                    ));
		groupedPlayers.add(new Group(ChatColor.LIGHT_PURPLE + "Technicy",      "technik"                   ));
		groupedPlayers.add(new Group(ChatColor.DARK_AQUA    + "Developerzy",   "dev"                       ));
		groupedPlayers.add(new Group(ChatColor.RED          + "Moderacja",     "kmod", "mod", "smod"       ));
		groupedPlayers.add(new Group(ChatColor.DARK_PURPLE  + "Pomocnicy",     "helper"                    ));
		groupedPlayers.add(new Group(ChatColor.LIGHT_PURPLE + "Przyjaciele",   "respected", "friends"      ));
		groupedPlayers.add(new Group(ChatColor.DARK_BLUE    + "Architekci",    "architect", "headarchitect"));
		groupedPlayers.add(new Group(ChatColor.AQUA         + "Designerzy",    "designer"                  ));
		groupedPlayers.add(new Group(ChatColor.GREEN        + "Builderzy",       "builder", "advbuilder"   ));
		groupedPlayers.add(new Group(ChatColor.DARK_GRAY    + "Familiarzy",    "familiar"                  ));
		groupedPlayers.add(new Group(ChatColor.GRAY         + "Playerzy",      "player"                    ));
		groupedPlayers.add(new Group(ChatColor.WHITE        + "Guesci",        "guest"                     ));
		
		for(Group group : groupedPlayers)
			showGroup(sender, group);
		
		return true;
	}
	
	
	private void showGroup(CommandSender sender, Group group)
	{
		List<Player> players = group.players;
		if(players.size() == 0)
			return;
		
		String playerlist = "";
		for(int i = 1; i < players.size(); ++i)
			playerlist += players.get(i).getDisplayName() + ", ";
		playerlist += players.get(0).getDisplayName();
		
		String msg = group.displayname + ChatColor.WHITE + ": " + playerlist;
		plugin.sendMessage(sender, msg, false);
	}
	
	
	@Override 
	protected boolean onlyPlayer()
	{
		return false;
	}

	@Override
	protected int minArgs()
	{
		return 0;
	}
	
	
	@Override
	protected String getPermission()
	{
		return null;
	}
}
