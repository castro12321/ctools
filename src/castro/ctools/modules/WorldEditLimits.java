/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;


/*
 * Limits worldedit selection size to 5*<blocks limit change>
 * Limits using radiuses bigger than 250
 */
public class WorldEditLimits extends CModule
{
	private static Permission permission;
	
    public WorldEditLimits(Permission permission)
    {
    	WorldEditLimits.permission = permission;
        plugin.registerEvents(this);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
    	reloadPlayerLimit(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
    {
    	event.getPlayer().performCommand("awe purge");
    }
    
    public static void reloadPlayerLimit(Player player)
    {
    	permission.playerAdd((String)null, player, "worldedit.limit");
    	player.performCommand("/limit " + getLimit(player));
    	permission.playerRemove((String)null, player, "worldedit.limit");
    }
	
	// Limit management
	private static int limit;
	
	private static void check(Player player, String permission, int limit)
	{
		if(player.hasPermission(permission))
			WorldEditLimits.limit = limit;
	}
	
	private static int getLimit(Player p)
	{
		limit = 0;
		
		final int k = 1000;
		final int m = 1000*k;
		check(p, "aliquam.testlow"     , 1*k);
		check(p, "aliquam.player"      , 50*k);
		check(p, "aliquam.familiar"    , 100*k);
		check(p, "aliquam.builder"     , 250*k);
		check(p, "aliquam.advbuilder"  , 500*k);
		check(p, "aliquam.designer"    , 1*m);
		check(p, "aliquam.architect"   , 1*m);
		check(p, "aliquam.mod"         , 5*m);
		
		check(p, "aliquam.welimit.100k", 100*k);
		check(p, "aliquam.welimit.500k", 500*k);
		check(p, "aliquam.welimit.2m"  , 2*m);
		check(p, "aliquam.welimit.5m"  , 5*m);
		
		check(p, "aliquam.welimit.x2"  , limit * 2);
		check(p, "aliquam.welimit.x22" , limit * 2);
		check(p, "aliquam.welimit.x3"  , limit * 3);
		check(p, "aliquam.welimit.x33" , limit * 3);
		check(p, "aliquam.welimit.vote", limit * 2);
		
		// Hard limit for regular players
		if(limit > 5*m)
			limit = 5*m;
		
		check(p, "aliquam.admin", -1);
		
		return limit;
	}
    
    
    @Override public boolean isListener()   { return true; }
    @Override public String[] getCommands() { return null; }
}