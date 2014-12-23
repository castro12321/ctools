package castro.ctools.modules.purger.players;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import castro.base.plugin.CPlugin;
import castro.ctools.Plugin;
import castro.ctools.modules.stats.PlayerData;
import castro.ctools.modules.stats.Stats;

public class PlayerPurger
{
	private final CPlugin plugin = Plugin.get();
	private final String playername;
	private final List<PlayerPurgerModule> modules = new ArrayList<>();
	
	public PlayerPurger(String playername)
	{
		this.playername = playername;
		
		PlayerData pData = Stats.sql.getPlayer(playername);
		modules.add(new ModuleWorlds(pData));
		// Disable other (low disk cost) modules. We need to delete worlds because they take lots of space.
		// Other modules will be replaced with better one later (check Trello)
		//modules.add(new ModulePermissions(pData));
		//modules.add(new ModuleEconomy(pData));
		//modules.add(new ModuleEssentials(pData));
		//modules.add(new ModuleMultiInventories(pData));
		// ModuleDat should be last! (We need the OfflinePlayer to be available during the whole operation that is stored inside the .dat files)
		//modules.add(new ModuleDat(pData));
	}
	
	public boolean run()
	{
		try
		{
    		// First, backup the player
    		for(PlayerPurgerModule module : modules)
    		{
    			plugin.log("- Backing up " + module.toString());
    			if(!module.backup())
    			{
    				plugin.log(ChatColor.RED + "ERROR: Cannot backup " + playername + ". Halting!");
    				return false;
    			}
    		}
    		
    		// If backup was successful, purge data
    		for(PlayerPurgerModule module : modules)
    		{
    			plugin.log("- Deleting " + module.toString());
    			if(!module.purge())
    			{
    				plugin.log(ChatColor.RED + "ERROR: Cannot delete " + playername + ". Halting!");
    				return false;
    			}
    		}
    		
    		// Finally removing player from stats
    		plugin.log("- Removing from cStats");
    		try
    		{
    			Stats.sql.deletePlayer(playername);
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
