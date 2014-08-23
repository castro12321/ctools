package castro.ctools.modules.purger.players;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import castro.base.plugin.CPlugin;
import castro.ctools.modules.stats.Stats;

public class PlayerPurger
{
	protected final CPlugin plugin;
	protected final String player;
	protected final List<PlayerPurgerModule> modules = new ArrayList<>();
	
	public PlayerPurger(String player)
	{
		this.player = player;
		this.plugin = castro.ctools.Plugin.get();
		
		modules.add(new ModuleWorlds(player));
		modules.add(new ModulePermissions(player));
		modules.add(new ModuleEconomy(player));
		modules.add(new ModuleEssentials(player));
		modules.add(new ModuleDat(player));
		modules.add(new ModuleMultiInventories(player));
	}
	
	public boolean run()
	{
		try
		{
    		// First, backup the player
    		for(PlayerPurgerModule module : modules)
    		{
    			//plugin.log("- Backing up " + module.toString());
    			if(!module.backup())
    			{
    				plugin.log(ChatColor.RED + "ERROR: Cannot backup " + player + ". Halting!");
    				return false;
    			}
    		}
    		
    		// If backup was successful, purge data
    		for(PlayerPurgerModule module : modules)
    		{
    			//plugin.log("- Deleting " + module.toString());
    			if(!module.purge())
    			{
    				plugin.log(ChatColor.RED + "ERROR: Cannot delete " + player + ". Halting!");
    				return false;
    			}
    		}
    		
    		// Finally removing player from stats
    		//plugin.log("- Removing from cStats");
    		try
    		{
    			Stats.sql.deletePlayer(player);
    		}
    		catch(SQLException e)
    		{
    			e.printStackTrace();
    			// Do nothing... Will try to delete him next time
    			return false;
    		}
    		
    		//plugin.log(ChatColor.GREEN + "Done!");
    		plugin.log("=== --- === --- ==="); // empty line
    		return true;
		}
		catch(Exception e)
		{
			plugin.log(ChatColor.RED + "Got unhandled exception");
			e.printStackTrace();
			return false;
		}
	}
}
