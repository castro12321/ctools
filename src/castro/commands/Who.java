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
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import castro.ctools.modules.groups.Group;
import castro.ctools.modules.groups.GroupManager;



public class Who extends BaseCommand
{
	@Override
	protected boolean prep()
	{		
		return true;
	}
	
	
	@Override
	protected boolean exec()
	{
		Server server = plugin.getServer();
		List<Player> onlinePlayers = Arrays.asList(server.getOnlinePlayers());
		HashMap<String, List<Player>> playersByGroup = getPlayersGrouped(onlinePlayers);
		
		showHeader(onlinePlayers.size(), server.getMaxPlayers());
		showGroups(playersByGroup);
		
		return true;
	}
	
	
	private void showHeader(int onlinePlayers, int maxPlayers)
	{
		plugin.sendMessage(sender, ChatColor.GOLD + "There are " + onlinePlayers + " out of maximum " + maxPlayers + " players on the server.", false);
		plugin.sendMessage(sender, ChatColor.GOLD + "Sorted by group:", false);
	}
	
	
	private HashMap<String, List<Player>> getPlayersGrouped(List<Player> onlinePlayers)
	{
		HashMap<String, List<Player>> playersByGroup = new HashMap<>();
		
		for(Player player : onlinePlayers)
		{
			Group group = GroupManager.get(player);
			if(!playersByGroup.containsKey(group.name))
				playersByGroup.put(group.name, new ArrayList<Player>());
			playersByGroup.get(group.name).add(player);
		}
		
		return playersByGroup;
	}
	
	
	private void showGroups(HashMap<String, List<Player>> playersByGroup)
	{
		for(String group : playersByGroup.keySet())
		{
			List<Player> players = playersByGroup.get(group);
			
			String playerlist = "";
			for(int i = 1; i < players.size(); ++i)
				playerlist += players.get(i).getDisplayName() + ", ";
			playerlist += players.get(0).getDisplayName();
			
			Group cGroup = GroupManager.get(group);
			String msg = cGroup.displayname + ChatColor.WHITE + ": " + playerlist;
			plugin.sendMessage(sender, msg, false);
		}
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
