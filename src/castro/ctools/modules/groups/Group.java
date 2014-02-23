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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class Group implements Comparable<Group>
{
	public final GroupType    type;
	public final int          order;
	public final String       name;
	public final String       displayname;
	public final String       prefix;
	public final String       suffix;
	public final List<Player> onlinePlayers = new ArrayList<Player>();
	
	
	public Group(GroupType type, int order, String name, String displayname)
	{
		this.type  = type;
		this.order = order;
		this.name  = name;
		this.displayname = ChatColor.translateAlternateColorCodes('&', displayname);
		this.prefix = ChatColor.YELLOW + "[" + this.displayname + ChatColor.YELLOW + "] " + ChatColor.WHITE;
		
		if(type == GroupType.STAFF)
			suffix = ChatColor.WHITE + ":";
		else
			suffix = ChatColor.WHITE + ":" + ChatColor.GRAY;
	}
	
	
	public void addPlayer(Player player)
	{
		onlinePlayers.add(player);
	}
	
	
	public void removePlayer(Player player)
	{
		onlinePlayers.remove(player);
	}


	@Override
    public int compareTo(Group other)
    {
	    if(order > other.order)
	    	return 1;
	    if(order < other.order)
	    	return -1;
	    return 0;
    }
}