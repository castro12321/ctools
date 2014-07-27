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

import java.util.Queue;

import org.bukkit.scheduler.BukkitScheduler;

import castro.ctools.modules.CModule;
import castro.ctools.modules.purger.players.PlayerPurger;

public class Purger extends CModule implements Runnable
{
	public static Purger instance;
	public final Backup backup;
	public final PurgerSQL purgerSQL;
	
	private final BukkitScheduler scheduler;
	private final int taskId;
	private final Queue<String> toBurn;
	
	
	public Purger()
	{
		plugin.log("Initializing purger...");
		instance  = this;
		backup    = new Backup();
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
			cleanOthers();
			return;
		}
		
		plugin.log("Burning " + playerToBurn);
		new PlayerPurger(playerToBurn).run();
	}
	
	private void cleanOthers()
	{
	}
	
	@Override public boolean isListener()   { return false; }
	@Override public String[] getCommands() { return null; }
}
