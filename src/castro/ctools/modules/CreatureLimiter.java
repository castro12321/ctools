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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerInteractEvent;

import castro.cWorlds.CPlot;
import castro.cWorlds.PlotsMgr;


public class CreatureLimiter extends CModule
{
	private static final int MOB_LIMIT  = 15;
	private HashMap<String, Integer> mobLimits = new HashMap<>();
	
	
	public CreatureLimiter()
	{	
		ConfigurationSection worlds = plugin.con.getConfigurationSection("worlds");
		if(worlds != null)
			for(String world : worlds.getKeys(false))
				mobLimits.put(world, worlds.getInt(world));
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(!sender.hasPermission("aliquam.admin"))
			return false;
		
		if(sender instanceof Player)
		{
			Player player = (Player)sender;
			if(args.length > 0)
			{
				Integer limit = Integer.parseInt(args[0]);
				setLimit(player.getWorld().getName(), limit);
			}
		}
		return false;
	}
	
	
	public void setLimit(String world, int limit)
	{	
		if(limit == MOB_LIMIT)
		{
			mobLimits.remove(world);
			plugin.con.set("worlds"+world, null);
		}
		
		mobLimits.put(world, limit);
		plugin.con.set("worlds."+world, limit);
		plugin.saveConfig();
	}
	
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		World world = event.getLocation().getWorld();
		String worldname = world.getName();
		
		int limit = MOB_LIMIT;
		if(mobLimits.containsKey(worldname))
			limit = mobLimits.get(worldname);
		
		if(limit == -1)
			return;
		
		SpawnReason reason = event.getSpawnReason();
		if(!reason.equals(SpawnReason.SPAWNER_EGG))
			event.setCancelled(true);
		removeRedundant(world, limit);
	}
	
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Action action = event.getAction();
		if(action.equals(Action.RIGHT_CLICK_BLOCK))
		{
			Player player = event.getPlayer();
			Material inHand = player.getItemInHand().getType();
			if(inHand.equals(Material.MONSTER_EGGS)
			|| inHand.equals(Material.MONSTER_EGG))
			{
				String worldname = player.getWorld().getName();
				CPlot plot = PlotsMgr.get(worldname);
				if(player.hasPermission("aliquam.admin"))
					return;
				if(plot != null)
					if(plot.isOwner(player.getName()))
						return;
				event.setCancelled(true);
			}
		}
	}
	
	
	private void removeRedundant(World world, int limit)
	{
		List<LivingEntity> entities = world.getLivingEntities();
		if(entities.size() < limit)
			return; // No need to check, though
		
		// Remove players
		List<LivingEntity> players = new ArrayList<>();
		for(LivingEntity entity : entities)
			if(entity instanceof Player)
				players.add(entity);
		entities.removeAll(players);
		
		// Remove redundant mobs
		int size = entities.size();
		//plugin.log("size: " + size + " limit: " + limit + " del: " + (size-limit));
		if(size > limit)
		{
			int delete = size-limit;
			while(delete --> 0)
				entities.get(0).remove(); // Removes oldest creature
		}
	}


	@Override public boolean isListener()	{ return true; }
	@Override public String[] getCommands()	{ return new String[] {"limitmobs"}; }
}