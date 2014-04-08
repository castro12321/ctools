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

package castro.ctools.modules;

import java.util.List;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitScheduler;

import castro.cWorlds.plots.PlotsMgr;
import castro.ctools.Plugin;


public class TimeLock extends CModule implements Runnable
{
	Plugin plugin = Plugin.get();
	
	public TimeLock()
	{
		BukkitScheduler scheduler = plugin.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(plugin, this, 200, 200);
	}
	
	
	@Override
	public void run()
	{
		final int DAY = 1000; // Day in Minecraft ticks
		List<World> worlds = plugin.getServer().getWorlds();
		for(World world : worlds)
			if(PlotsMgr.get(world) == null)
				world.setTime(DAY);
	}
	
	
	@Override public boolean isListener()	{ return false; }
	@Override public String[] getCommands()	{ return null; }
}