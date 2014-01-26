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

import net.minecraft.server.v1_7_R1.WorldServer;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import castro.ctools.Plugin;


public class GameModeListener implements Listener 
{
	@EventHandler public void onDrop(ItemSpawnEvent event)
	{
		if(event.getLocation().getWorld().getName().startsWith("_")) // ignore cWorlds plots
			return;
		event.setCancelled(true);
	}
	@EventHandler 
	public void onProjectileLaunch(ProjectileLaunchEvent event)
	{
		if(event.getEntity().getWorld().getName().startsWith("_")) // ignore cWorlds plots
			return;
		event.setCancelled(true);
	}
	
	
	@EventHandler 
	public void onPlayerItemConsume(PlayerItemConsumeEvent event)
	{
		if(event.getPlayer().getWorld().getName().startsWith("_")) // ignore cWorlds plots
			return;
		event.setCancelled(true);
	}
	
	
	@EventHandler
	public void onBlockFall(EntityChangeBlockEvent event)
	{
		if(event.getEntity().getWorld().getName().startsWith("_")) // ignore cWorlds plots
			return;
		
		if (event.getEntityType() == EntityType.FALLING_BLOCK)
			event.setCancelled(true);
	}
	
	
	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event)
	{
		if(event.getBlock().getWorld().getName().startsWith("_")) // ignore cWorlds plots
			return;
		
		Material changed = event.getChangedType();
		Material material = event.getBlock().getType();
		if(!redstoneMaterials.contains(changed)
		&& !redstoneMaterials.contains(material))
			event.setCancelled(true);
	}
	
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Action action = event.getAction();
		if(action.equals(Action.RIGHT_CLICK_BLOCK))
		{
			Block clicked = event.getClickedBlock();
			if(clicked.getType().equals(Material.REDSTONE_LAMP_OFF))
			{
				Player player = event.getPlayer();
				if(Plugin.worldguard.canBuild(player, clicked))
					staticSet(clicked, Material.REDSTONE_LAMP_ON);
			}
		}
	}
	
	
	private void staticSet(Block block, Material material)
	{
		WorldServer ws = ((CraftWorld)block.getWorld()).getHandle();
		boolean old = ws.isStatic;
		ws.isStatic = true;
		block.setType(material);
		ws.isStatic = old;
	}
	
	
	
	private static List<Material> redstoneMaterials = null;
	static
	{
		if(redstoneMaterials == null)
			redstoneMaterials = Arrays.asList(getRedstoneMaterials());
	}
	
	
	private static Material[] getRedstoneMaterials()
	{
		return new Material[]
		{
			// Allow water to flow
			Material.WATER,
			Material.STATIONARY_WATER,
			Material.LAVA,
			Material.STATIONARY_LAVA,
			
			// Allow redstone to execute
			Material.REDSTONE_WIRE,
			Material.REDSTONE_COMPARATOR_OFF,
			Material.REDSTONE_COMPARATOR_ON,
			Material.REDSTONE_TORCH_OFF,
			Material.REDSTONE_TORCH_ON,
			Material.DIODE_BLOCK_OFF,
			Material.DIODE_BLOCK_ON,
			
			// Allow items to execute
			Material.WOOD_BUTTON,
			Material.STONE_BUTTON,
			Material.WOOD_PLATE,
			Material.STONE_PLATE,
			Material.IRON_PLATE,
			Material.GOLD_PLATE,
			Material.LEVER,
			Material.TRIPWIRE,
			Material.TRIPWIRE_HOOK,
			Material.REDSTONE_BLOCK,
			Material.DAYLIGHT_DETECTOR,
			Material.PISTON_BASE,
			Material.PISTON_EXTENSION,
			Material.PISTON_MOVING_PIECE,
			Material.PISTON_STICKY_BASE,
			
			Material.WOODEN_DOOR,
			Material.IRON_DOOR_BLOCK,
		};
	}
}