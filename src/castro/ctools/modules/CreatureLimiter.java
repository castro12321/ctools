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

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;


public class CreatureLimiter extends CModule
{
	private static final int DEFAULT_LIMIT = 15;
	private HashMap<String, Integer> limits = new HashMap<>();
	
	
	public CreatureLimiter()
	{	
		ConfigurationSection worlds = plugin.con.getConfigurationSection("worlds");
		if(worlds != null)
			for(String world : worlds.getKeys(false))
				limits.put(world, worlds.getInt(world));
	}
	
	
	public void setLimit(String world, int limit)
	{	
		if(limit == DEFAULT_LIMIT)
		{
			limits.remove(world);
			plugin.con.set("worlds"+world, null);
		}
		
		limits.put(world, limit);
		plugin.con.set("worlds."+world, limit);
		plugin.saveConfig();
	}
	
	
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		World world = event.getLocation().getWorld();
		String worldname = world.getName();
		
		int limit = DEFAULT_LIMIT;
		if(limits.containsKey(worldname))
			limit = limits.get(worldname);
		
		if(limit == -1)
			return;
		
		SpawnReason reason = event.getSpawnReason();
		if(!reason.equals(SpawnReason.SPAWNER_EGG))
		{
			event.setCancelled(true);
			return;
		}
		
		removeRedundant(world, limit);
	}
	
	
	private void removeRedundant(World world, int limit)
	{
		int mobs = 0;
		List<Entity> entities = world.getEntities();
		List<Entity> mobsToDelete = new ArrayList<Entity>();
		for(Entity entity : entities)
		{
			if(entity instanceof LivingEntity)
			{
				if(!(entity instanceof Player))
				{
					if(mobs >= limit)
						mobsToDelete.add(entity);
					else
						mobs++;
				}
			}
		}
		
		//int toDelete = mobs-limit;
		//for(int i = 0; i < toDelete; ++i)
		//	mobsList.get(toDelete-i).remove();
		plugin.log(limit + " z " + entities.size() + " del: " + mobsToDelete.size());
		int size = mobsToDelete.size();
		for(int i = 0; i < size; ++i)
			mobsToDelete.get(/**size-i-1/*/i/**/).remove();
	}


	@Override public boolean isListener()	{ return true; }
	@Override public String[] getCommands()	{ return null; }
}