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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;

import castro.cWorlds.CPlot;
import castro.cWorlds.PlotsMgr;
import castro.ctools.Plugin;


public class EntityLimiter extends CModule
{
	private final EntityLimits  limits;
	private final EntityCleaner cleaner;
	
	public EntityLimiter()
	{
		limits  = new EntityLimits(plugin);
		cleaner = new EntityCleaner(plugin, limits);
	}
	
	
	public void cancelNotMember(Cancellable cancellable, PlayerEvent playerEvent)
	{
		cancelNotMember(playerEvent.getPlayer(), cancellable);
	}
	public void cancelNotMember(Entity entity, Cancellable event)
	{
		if(entity instanceof Player) cancelNotMember((Player)entity, event);
	}
	public void cancelNotMember(Player player, Cancellable event)
	{
		CPlot plot = PlotsMgr.get(player.getWorld());
		if(!plot.isMember(player.getName()))
			event.setCancelled(true);
	}
	
	
	@EventHandler public void onItemDrop(PlayerDropItemEvent event)   { cancelNotMember(event, event); }
	@EventHandler public void onShot    (ProjectileLaunchEvent event) { cancelNotMember(event.getEntity().getShooter(), event); }
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemSpawn(ItemSpawnEvent event)               { cleaner.cleanItems(event.getLocation().getWorld()); }
	@EventHandler(priority = EventPriority.MONITOR)
	public void onProjectileLaunch(ProjectileLaunchEvent event) { cleaner.cleanItems(event.getEntity().getWorld()); }
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityPlace(VehicleCreateEvent event)         { cleaner.cleanItems(event.getVehicle().getWorld()); }
	
	
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
					if(plot.isMember(player.getName()))
						return;
				event.setCancelled(true);
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		World world = event.getLocation().getWorld();
		
		int limit = limits.getMobLimit(world);
		if(limit == -1)
			return;
		
		SpawnReason reason = event.getSpawnReason();
		if(!reason.equals(SpawnReason.SPAWNER_EGG))
			event.setCancelled(true);
		
		cleaner.cleanMobs(world);
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
				limits.setLimit(player.getWorld(), limit);
				return true;
			}
		}
		return false;
	}


	@Override public boolean isListener()	{ return true; }
	@Override public String[] getCommands()	{ return new String[] {"limitmobs"}; }
}


class EntityLimits
{
	private static final int DEFAULT_MOB_LIMIT  = 15;
	
	private final Plugin plugin;
	private HashMap<String, Integer> mobLimits = new HashMap<>();
	
	
	EntityLimits(Plugin plugin)
	{
		this.plugin = plugin;
		ConfigurationSection worlds = plugin.con.getConfigurationSection("worlds");
		if(worlds != null)
			for(String world : worlds.getKeys(false))
				mobLimits.put(world, worlds.getInt(world));
	}
	
	
	int getMobLimit(World world)
	{
		String worldname = world.getName();
		int limit = DEFAULT_MOB_LIMIT;
		if(mobLimits.containsKey(worldname))
			return mobLimits.get(worldname);
		return limit;
	}
	
	
	int getItemsLimit(World world)
	{
		int limit = getMobLimit(world);
		if(limit != -1)
			return limit * 5;
		return limit;
	}
	
	
	void setLimit(World world, int limit)
	{
		String worldname = world.getName();
		if(limit == DEFAULT_MOB_LIMIT)
		{
			mobLimits.remove(worldname);
			plugin.con.set("worlds"+worldname, null);
		}
		mobLimits.put(worldname, limit);
		plugin.con.set("worlds."+worldname, limit);
		plugin.saveConfig();
	}
}


class EntityCleaner
{
	private final Plugin plugin;
	private final EntityLimits limits;
	
	
	public EntityCleaner(Plugin plugin, EntityLimits limits)
    {
		this.plugin = plugin;
		this.limits = limits;
    }
	
	public void cleanMobs(World world)
	{
		int mobLimit = limits.getMobLimit(world);
		List<LivingEntity> entities = world.getLivingEntities();
		if(entities.size() < mobLimit)
			return; // No need to check, though
		
		List<? extends LivingEntity> players = Arrays.asList(plugin.getServer().getOnlinePlayers());
		entities.removeAll(players);
		
		removeRedundant(entities, mobLimit);
	}
	
	
	public void cleanItems(World world)
	{
		int itemsLimit = limits.getItemsLimit(world);
		Collection<Entity> entities = world.getEntitiesByClasses(
			EnderCrystal.class, EnderSignal.class, ExperienceOrb.class, FallingBlock.class, 
			Firework.class, Item.class, Projectile.class, Vehicle.class);
		
		if(entities.size() < itemsLimit)
			return; // No need to check, though
		
		removeRedundant(new ArrayList<>(entities), itemsLimit);
	}
	
	
	private void removeRedundant(List<? extends Entity> list, int limit)
	{
		int size = list.size();
		//plugin.log("size: " + size + " limit: " + limit + " del: " + (size-limit));
		if(size > limit)
		{
			int delete = size-limit;
			while(delete --> 0)
				list.get(0).remove(); // Removes oldest creature
		}
	}
}