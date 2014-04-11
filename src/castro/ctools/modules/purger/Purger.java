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
		purgerSQL = new PurgerSQL(plugin);
		scheduler = plugin.getServer().getScheduler(); 
		taskId    = scheduler.scheduleSyncRepeatingTask(plugin, this, 1, 1);
		toBurn    = purgerSQL.getPlayersToBurn();
	}
	
	
	@Override
	public void run()
	{
		String playerToBurn = toBurn.poll();
		if(playerToBurn == null)
			scheduler.cancelTask(taskId);
		
		List<PurgeModule> modules = new ArrayList<>();
		modules.add(new CWorldsModule());
		// handle old server rank
		// handle plot world
		// handle economy account
		modules.add(new EssentialsPurger());
		// handle .dat file
		
		for(PurgeModule module : modules)
		{
			module.backup(playerToBurn);
			module.purge (playerToBurn);
		}
		
		// Finally removing player from stats
		Stats.sql.deletePlayer(playerToBurn);
	}
	
	
	@Override public boolean isListener()   { return false; }
	@Override public String[] getCommands() { return null; }
}
