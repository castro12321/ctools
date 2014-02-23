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
import java.util.Collections;
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
		int onlinePlayers = server.getOnlinePlayers().length;
		int maxPlayers    = server.getMaxPlayers();
		plugin.sendMessage(sender, ChatColor.GOLD + "There are " + onlinePlayers + " out of maximum " + maxPlayers + " players on the server.", false);
		plugin.sendMessage(sender, ChatColor.GOLD + "Sorted by group:", false);
		
		List<Group> groups = new ArrayList<>(GroupManager.getAllGroups());
		Collections.sort(groups);
		for(Group group : groups)
			showGroup(group);
		
		return true;
	}
	
	
	private void showGroup(Group group)
	{
		List<Player> players = group.onlinePlayers;
		if(players.isEmpty())
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
