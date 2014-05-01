package castro.ctools;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import castro.ctools.modules.groups.Group;
import castro.ctools.modules.groups.GroupManager;

public class PermissionsBridge
{
	public static void setGroup(String playername, String group)
	{
		Player player = Bukkit.getPlayerExact(playername);
		removeFromCGroup(player);
		Plugin.dispatchConsoleCommand("pex user " + playername + " group set " + group);
		addToCGroup(player);
		Plugin.get().reloadPlayer(player);
	}
	
	
	public static boolean addToGroup(Player player, String group)
	{
		removeFromCGroup(player);
		if(Plugin.permission.playerAddGroup(player, group))
		{
    		addToCGroup(player);
    		Plugin.get().reloadPlayer(player);
    		return true;
		}
		return false;
	}
	
	
	public static void removeFromGroup(Player player, String rankFrom)
	{
		removeFromCGroup(player);
		Plugin.permission.playerRemoveGroup(player, rankFrom);
		addToCGroup(player);
		Plugin.get().reloadPlayer(player);
	}
	
	
	private static void removeFromCGroup(Player player)
	{
		if(player != null)
		{
    		Group group = GroupManager.get(player);
    		group.removePlayer(player);
		}
	}
	
	
	private static void addToCGroup(Player player)
	{
		if(player != null)
		{
    		Group group = GroupManager.get(player);
    		group.addPlayer(player);
		}
	}
}