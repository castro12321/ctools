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
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;


public class GameModeListener implements Listener 
{
	private static List<Material> redstoneMaterials = null;
	private static List<Material> blockPhysics = null;
	
	
	@EventHandler public void onDrop(ItemSpawnEvent event)						{ event.setCancelled(true); }
	@EventHandler public void onProjectileLaunch(ProjectileLaunchEvent event)	{ event.setCancelled(true); }
	@EventHandler public void on1PlayerItemConsume(PlayerItemConsumeEvent event){ event.setCancelled(true); }
	
	
	@EventHandler
	public void onBlockFall(EntityChangeBlockEvent event)
	{
		if (event.getEntityType() == EntityType.FALLING_BLOCK)
			event.setCancelled(true);
	}
	
	
	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event)
	{
		Block block = event.getBlock();
		
		Material changed = event.getChangedType();
		
		if(redstoneMaterials.contains(changed))
		{
			Material checked = block.getType();
			if(blockPhysics.contains(checked))
				event.setCancelled(true);
		}
		else
			event.setCancelled(true);
	}
	
	
	static
	{
		if(redstoneMaterials == null)
		{
			redstoneMaterials = Arrays.asList(getRedstoneMaterials());
			blockPhysics	  = Arrays.asList(getBlockedMaterials());
		}
	}
	
	
	private static Material[] getRedstoneMaterials()
	{
		return new Material[]
			{
			// We want to allow water to flow
			/*
			Material.WATER,
			Material.STATIONARY_WATER,
			Material.LAVA,
			Material.STATIONARY_LAVA,
			*/
			// And allow redstone to execute
			Material.REDSTONE_WIRE,
			Material.REDSTONE_COMPARATOR_OFF,
			Material.REDSTONE_COMPARATOR_ON,
			Material.REDSTONE_TORCH_OFF,
			Material.REDSTONE_TORCH_ON,
		};
	}
	
	
	private static Material[] getBlockedMaterials()
	{
		return new Material[]
			{
				Material.REDSTONE_LAMP_OFF,
				Material.REDSTONE_LAMP_ON
			};
	}
}