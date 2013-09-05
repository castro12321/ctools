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

package castro.EventListeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import castro.ctools.Plugin;


public class GameModeListener implements Listener 
{
	private Plugin plugin = Plugin.get();
	private static List<Material> retainedMaterials = null;
	
	
	//private boolean creative(Location loc)	{ return creative(loc.getWorld()); }
	//private boolean survival(Location loc)	{ return survival(loc.getWorld()); }
	//private void cancelIfSurvival(Cancellable event, World world) { if(survival(world)) event.setCancelled(true); }
	private boolean creative(World world)	{ return !survival(world); }
	private boolean survival(World world)	{ return world.getName().startsWith("s"); }
	private void cancelIfCreative(Cancellable event, World world) { if(creative(world)) event.setCancelled(true); }
	
	
	@EventHandler public void onDrop(ItemSpawnEvent event)						{ cancelIfCreative(event, event.getLocation().getWorld()); }
	@EventHandler public void onProjectileLaunch(ProjectileLaunchEvent event)	{ cancelIfCreative(event, event.getEntity().getWorld()); }
	@EventHandler public void onCreatureSpawn(CreatureSpawnEvent event)			{ cancelIfCreative(event, event.getLocation().getWorld()); }
	@EventHandler public void onPlayerItemConsume(PlayerItemConsumeEvent event)	{ cancelIfCreative(event, event.getPlayer().getWorld()); }
	
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event)
	{
		InventoryType invType = event.getInventory().getType();
		
		if(invType.equals(InventoryType.ENDER_CHEST))
			cancelIfCreative(event, event.getPlayer().getWorld());
	}
	
	
	@EventHandler
	public void blockCanBuild(BlockCanBuildEvent event)
	{
		// TODO: check docs
		plugin.log("can build? " + event.getBlock().getType());
		if(creative(event.getBlock().getWorld()))
			event.setBuildable(true);
		
		//BlockEvent
	}
	
	
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event)
	{
		// TODO: test it
		plugin.log("GOT FROM " + event.getBlock().getType() + " TO " + event.getToBlock().getType());
		
		Block from = event.getBlock();
		World world = from.getWorld();
		if(survival(world))
			return;
		
		Material fromMat = from.getType();
		if(retainedMaterials.contains(fromMat))
			return;
		
		Block to = event.getToBlock();
		if(to.getType().equals(Material.AIR))
			event.setCancelled(true);
	}
	
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if(creative(event.getPlayer().getWorld()))
			return;
		if(event.getPlayer().hasPermission("aliquam.mod"))
			return;
		
		String command = event.getMessage().split(" ")[0]; // Get first word
		command = command.substring(1);
		
		switch(command)
		{
		default:
			event.setCancelled(true);
			plugin.sendMessage(event.getPlayer(), "&cTa komenda jest zablokowana na mapie survival");
		
		case "kingdom":
		case "kingdoms":
		case "mail":
		case "m":
		case "msg":
		case "tell":
		case "votekick":
		case "voteban":
		case "modreq":
		case "afk":
		case "who":
		case "list":
		}
	}
	
	
	
	static
	{
		if(retainedMaterials == null)
		{
			final Material[] retained = new Material[]
				{
					Material.WATER,
					Material.STATIONARY_WATER,
					Material.LAVA,
					Material.STATIONARY_LAVA
				};
				retainedMaterials = Arrays.asList(retained);
		}
	}
}