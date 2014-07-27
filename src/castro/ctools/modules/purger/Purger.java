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

package castro.ctools.modules.purger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.bukkit.scheduler.BukkitScheduler;

import castro.ctools.modules.CModule;
import castro.ctools.modules.stats.Stats;

public class Purger extends CModule implements Runnable
{
	private final PurgerSQL purgerSQL;
	private final BukkitScheduler scheduler;
	private final int taskId;
	private final Queue<String> toBurn;
	
	
	public Purger()
	{
		plugin.log("Initializing purger...");
		purgerSQL = new PurgerSQL(plugin);
		toBurn    = purgerSQL.getPlayersToBurn();
		scheduler = plugin.getServer().getScheduler();
		taskId    = scheduler.scheduleSyncRepeatingTask(plugin, this, 1, 1);
		plugin.log("Initialized! Will burn " + toBurn.size() + " players");
	}
	
	
	@Override
	public void run()
	{
		String playerToBurn = toBurn.poll();
		if(playerToBurn == null)
		{
			scheduler.cancelTask(taskId);
			return;
		}
		
		plugin.log("Burning " + playerToBurn);
		
		List<Module> modules = new ArrayList<>();
		modules.add(new ModuleWorlds());
		modules.add(new ModulePermissions());
		modules.add(new ModuleEconomy());
		modules.add(new ModuleEssentials());
		modules.add(new ModuleDat());
		//modules.add(new ModuleMultiInventories());
		
		// First, backup all players
		for(Module module : modules)
		{
			plugin.log("- backing up " + module.toString());
			if(!module.backup(playerToBurn))
			{
				plugin.log("Cannot backup " + playerToBurn + ". Halting!");
				return;
			}
		}
		
		// If backup was successful, purge data
		for(Module module : modules)
		{
			plugin.log("- deleting " + module.toString());
			if(!module.purge (playerToBurn))
			{
				plugin.log("Cannot delete " + playerToBurn + ". Halting!");
				return;
			}
		}
		
		// Finally removing player from stats
		plugin.log("Removing from cStats");
		try
		{
			Stats.sql.deletePlayer(playerToBurn);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			// Do nothing... Will try to delete him next time
		}
		
		plugin.log("Done!");
		plugin.log(""); // empty line
	}
	
	
	@Override public boolean isListener()   { return false; }
	@Override public String[] getCommands() { return null; }
}
