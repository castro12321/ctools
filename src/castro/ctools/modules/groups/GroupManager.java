/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.groups;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import castro.ctools.Plugin;
import castro.ctools.modules.CModule;

public class GroupManager extends CModule
{
	private static HashMap<String, Group> groupsByName = new HashMap<>();
	private final GroupsSQL groupsSQL;
	
	
	public GroupManager()
	{
		groupsSQL = new GroupsSQL(plugin);
		reloadGroups(groupsSQL);
		
		final int second = 20;
		Runnable refresher = new Runnable()
				{
					@Override public void run()
					{
						reloadGroups(groupsSQL);
					}
				};
		plugin.scheduleSyncRepeatingTask(refresher, 900*second, 900*second);
	}
	
	
	private void reloadGroups(GroupsSQL sql)
	{
		groupsByName.clear();
		
		List<Group> groups = sql.getAllGroups();
		for(Group group : groups)
			groupsByName.put(group.name, group);
		
		for(Player player : Bukkit.getOnlinePlayers())
		{
    		Group  group  = get(player);
    		group.addPlayer(player);
		}
	}
	
	
	public static Group get(Player player)
	{
		String[] groups = Plugin.permission.getPlayerGroups(player);
		Group highestGroup = get(groups[0]);
		for(String group : groups)
		{
			Group next = get(group);
			if(next.type.priority > highestGroup.type.priority)
				highestGroup = next;
		}
		return highestGroup;
	}
	
	
	public static Group get(String groupname)
	{
		Group group = groupsByName.get(groupname);
		if(group == null)
			group = new Group(GroupType.REGULAR, 1337, groupname, groupname);
		return group;
	}
	
	
	public static Collection<Group> getAllGroups()
	{
		return groupsByName.values();
	}
	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		Group  group  = get(player);
		group.addPlayer(player);
	}
	
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		Group  group  = get(player);
		group.removePlayer(player);
	}
	
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event)
	{
		Player player = event.getPlayer();
		Group  group  = get(player);
		group.removePlayer(player);
	}
	
	
	@Override public boolean isListener()   { return true; }
	@Override public String[] getCommands() { return null; }
}
