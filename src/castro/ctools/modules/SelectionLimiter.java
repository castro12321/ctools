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

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import castro.base.plugin.CUtils;
import castro.ctools.Plugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;


/*
 * Limits worldedit selection size to 5*<blocks limit change>
 * Limits using radiuses bigger than 250
 */
public class SelectionLimiter extends CModule
{
	private Map<String, String> WorldEditCommands = WorldEdit.getInstance().getCommands();
	
	public SelectionLimiter()
	{
		plugin.registerEvents(this);
		WorldEditCommands = WorldEdit.getInstance().getCommands();
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommandPreProcess(PlayerCommandPreprocessEvent event)
	{
		Player player  = event.getPlayer();
		String message = event.getMessage();
		String[] split = message.split(" ");
		String command = split[0].substring(1);
		if(command.startsWith("/"))
			command = command.substring(1);
		
		// VoxelSniper
		if(command.startsWith("v "))
		{
			if(split.length > 1)
			{
				int id = CUtils.convert(split[1], Integer.class, 0);
				if(id == 175) // tall flower, 175:6 crashes the client
				{
					event.setCancelled(true);
					plugin.sendMessage(player, "&cWarning: Cannot use this block");
				}
			}
		}
		
		// WorldEdit
		if(command.startsWith("undo ") // notice space at the end
		|| command.startsWith("redo ")
		|| command.startsWith("u "))
		{
			event.setCancelled(true);
			plugin.sendMessage(player, "&cWarning: You can only undo/redo one action at a time");
			return;
		}
		
		// Ignore not WE commands
		if(!WorldEditCommands.containsKey(command)
		&& !WorldEditCommands.containsKey("/"+command))
			return;
		
		if(command.startsWith("sel")
		|| command.startsWith("size")
		|| command.startsWith("limit")
		|| command.startsWith("expand")
		|| command.startsWith("rotate") 
		|| command.startsWith("contract"))
			return;
		
		int selectionMultiplier = 1;
		if(command.startsWith("stack ")
		&& split.length > 1)
		{
			selectionMultiplier = CUtils.convert(split[1], Integer.class, 1);
			if(selectionMultiplier > 10)
				selectionMultiplier *= 100;
			if(selectionMultiplier > 50)
				selectionMultiplier *= 100; // Uh, maybe some time i'll just block it xD
		}
		
		if(isRadiusTooBig(message))
		{
			event.setCancelled(true);
			plugin.sendMessage(player, "&cWarning: Provided radius is too big");
		}
		else if(isSelectionTooBig(player, selectionMultiplier))
		{
			event.setCancelled(true);
			plugin.sendMessage(player, "&cWarning: Your selection is too big. To reset your selection, type &a//sel");
		}
	}
	
	
	@EventHandler()
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		final PlayerInteractEvent event = e; 
		plugin.scheduleSyncDelayedTask(new Runnable()
		{
			@Override
			public void run()
			{
				Action action = event.getAction();
				Player player = event.getPlayer();
				if (action == Action.RIGHT_CLICK_BLOCK
				||  action == Action.LEFT_CLICK_BLOCK)
					if (event.getMaterial() == Material.WOOD_AXE
					&&  isSelectionTooBig(player, 1))
						plugin.sendMessage(player, "&cWarning: Your selection is too big");
			}
		});
		
	}
	
	
	private boolean isRadiusTooBig(String command)
	{
		String[] words = command.split(" ");
		for(String word : words)
		{
			// We can safely block each number over 200
			// because blocks have ids below 200
			int number = CUtils.convert(word, Integer.class, 0);
			if(number > 200)
				return true;
		}
		return false;
	}
	
	
	private boolean isSelectionTooBig(Player player, int selectionMultiplier)
	{
		LocalSession session = Plugin.worldedit.getSession(player);
		int limit = session.getBlockChangeLimit();
		if(limit == -1)
			return false;
		try
		{
			Region selection = session.getSelection(session.getSelectionWorld());
			return
				selection.getWidth()  > 750
			||  selection.getLength() > 750
			||  selection.getArea()   > 5 * 1000 * 1000 // 5mln blocks is too much ;)
			||  selection.getArea() * selectionMultiplier > limit * 10
			||  selection.getArea()   > limit * 10; // In case of integer overflow above
		}
		catch (IncompleteRegionException e)
		{
			// Nothing
		}
		return false;
	}
	
	
	@Override public boolean isListener()	{ return true; }
	@Override public String[] getCommands()	{ return null; }
}