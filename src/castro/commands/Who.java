/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import castro.ctools.modules.groups.Group;
import castro.ctools.modules.groups.GroupManager;


public class Who extends CCommand
{
	@Override
	protected boolean prepare()
	{		
		return true;
	}
	
	
	@Override
	protected boolean execute()
	{
		int onlinePlayers = Bukkit.getOnlinePlayers().size();
		int maxPlayers    = Bukkit.getMaxPlayers();
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
	public String[] neededPermissions()
	{
		return null;
	}
}
