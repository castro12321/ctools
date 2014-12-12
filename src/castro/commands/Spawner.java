/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

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


public class Spawner extends CCommand
{
	private Mob mob;
	private Integer delay = 0;
	private Location target;
	
	@Override
	protected boolean prepare()
	{
		World world = senderPlayer.getWorld();
		CPlot plot  = PlotsMgr.get(world);
		if(plot != null)
			if(plot.is(senderPlayer, MemberType.MEMBER))
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
	                    target = LocationUtil.getTarget(senderPlayer);
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
	protected boolean execute()
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
	public String[] neededPermissions()
	{
		return null;
	}
}
