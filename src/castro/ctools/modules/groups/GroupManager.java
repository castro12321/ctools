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
		
		Player[] players = Bukkit.getOnlinePlayers();
		for(Player player : players)
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
