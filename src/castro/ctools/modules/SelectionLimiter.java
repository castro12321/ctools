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
import com.sk89q.worldedit.regions.Region;


/*
 * Limits worldedit selection size to 5*<blocks limit change>
 * Limits using radiuses bigger than 250
 */
public class SelectionLimiter extends CModule
{
	
	public SelectionLimiter()
	{
		Plugin.get().registerEvents(this);
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommandPreProcess(PlayerCommandPreprocessEvent event)
	{
		Player player  = event.getPlayer();
		String message = event.getMessage();
		String[] split = message.split(" ");
		
		// VoxelSniper
		if(message.startsWith("/v "))
		{
			if(split.length > 1)
			{
				int id = CUtils.convert(split[1], Integer.class, 0);
				if(isBlockForbidden(id))
				{
					event.setCancelled(true);
					plugin.sendMessage(player, "&cWarning: Cannot use this block");
				}
			}
		}
		
		// WorldEdit
		// Ignore commands
		if(!message.startsWith("//")
		||  message.startsWith("//sel")
		||  message.startsWith("//size")
		||  message.startsWith("//limit")
		||  message.startsWith("//expand")
		||  message.startsWith("//rotate") 
		||  message.startsWith("//contract"))
			return;
		
		if(message.startsWith("//undo ") // notice space at the end
		|| message.startsWith("//redo "))
		{
			event.setCancelled(true);
			plugin.sendMessage(player, "&cWarning: You can only undo/redo one action at a time");
			return;
		}
		
		int selectionMultiplier = 1;
		if(message.startsWith("//stack ")
		&& split.length > 1)
			selectionMultiplier = CUtils.convert(split[1], Integer.class, 0);
		
		if(isRadiusTooBig(message))
		{
			event.setCancelled(true);
			plugin.sendMessage(player, "&cWarning: Provided radius is too big");
		}
		else if(isSelectionTooBig(player, selectionMultiplier))
		{
			event.setCancelled(true);
			plugin.sendMessage(player, "&cWarning: Your selection is too big");
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
			||  selection.getArea()   > limit * 10 // In case of integer overflow below
			||  selection.getArea() * selectionMultiplier > limit * 10;
		}
		catch (IncompleteRegionException e)
		{
			// Nothing
		}
		return false;
	}
	
	
	private boolean isBlockForbidden(int id)
	{
		switch(id)
		{
		case 6:
		case 7:
		case 26:
		case 27:
		case 28:
		case 29:
		case 31:
		case 32:
		case 33:
		case 34:
		case 36:
		case 37:
		case 38:
		case 39:
		case 40:
		case 50:
		case 51:
		case 55:
		case 59:
		case 63:
		case 64:
		case 65:
		case 66:
		case 68:
		case 69:
		case 70:
		case 71:
		case 72:
		case 75:
		case 76:
		case 77:
		case 81:
		case 83:
		case 90:
		case 92:
		case 93:
		case 94:
		case 96:
		case 97:
		case 104:
		case 105:
		case 106:
		case 111:
		case 115:
		case 119:
		case 127:
		case 131:
		case 132:
		case 140:
		case 141:
		case 142:
		case 143:
		case 147:
		case 148:
		case 149:
		case 150:
		case 157:
		case 167:
		case 175:
			return true;
		}
		return false;
	}
	
	
	@Override public boolean isListener()	{ return true; }
	@Override public String[] getCommands()	{ return null; }
}