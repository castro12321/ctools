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

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import castro.ctools.Plugin;
import castro.ctools.modules.CModule;

public class GroupManager extends CModule
{
	private static HashMap<String, Group> groupsByName = new HashMap<>();
	
	
	public GroupManager(Plugin plugin)
	{
		reloadGroups(new GroupsSQL(plugin));
	}
	
	
	private void reloadGroups(GroupsSQL sql)
	{
		List<Group> groups = sql.getAllGroups();
		for(Group group : groups)
			groupsByName.put(group.name, group);
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
			group = new Group(GroupType.REGULAR, groupname, groupname);
		return group;
	}
	
	
	@Override public boolean isListener()   { return false; }
	@Override public String[] getCommands() { return null; }
}
