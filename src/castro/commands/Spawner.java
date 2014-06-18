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

package castro.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;

import castro.base.plugin.CUtils;
import castro.cWorlds.Utils.MemberType;
import castro.cWorlds.plots.CPlot;
import castro.cWorlds.plots.PlotsMgr;

import com.earth2me.essentials.Mob;
import com.earth2me.essentials.utils.LocationUtil;
import com.earth2me.essentials.utils.StringUtil;


public class Spawner extends BaseCommand
{
	private Mob mob;
	private Integer delay = 0;
	private Location target;
	
	@Override
	protected boolean prep()
	{
		World world = player.getWorld();
		CPlot plot  = PlotsMgr.get(world);
		if(plot != null)
			if(plot.is(player, MemberType.MEMBER))
			{
				switch(args.length)
				{
				case 0:
					plugin.sendMessage(sender, "Available mobs: " + StringUtil.joinList(Mob.getMobList()));
					return false;
				default:
				case 2:
					delay = CUtils.convert(args[1], Integer.class);
				case 1:
					mob = Mob.fromName(args[0]);
					try
                    {
	                    target = LocationUtil.getTarget(player);
                    }
                    catch(Exception e)
                    {
	                    e.printStackTrace();
                    }
				}
			}
		return target != null && mob != null && delay != null;
	}
	
	
	@Override
	protected boolean exec()
	{
		CreatureSpawner spawner = (CreatureSpawner)target.getBlock().getState();
		spawner.setSpawnedType(mob.getType());
		spawner.setDelay(delay);
		spawner.update();
		plugin.sendMessage(sender, "Spawner updated! " + mob.getType());
		return true;
	}
	
	
	@Override 
	protected boolean onlyPlayer()
	{
		return true;
	}
	
	
	@Override
	protected int minArgs()
	{
		return 0;
	}
	
	
	@Override
	protected String getPermission()
	{
		return null;
	}
}
