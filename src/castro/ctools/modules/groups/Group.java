/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

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