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

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import castro.base.plugin.CUtils;
import castro.ctools.Plugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.regions.Region;


/*
 * Limits worldedit selection size to 5*<blocks limit change>
 * Limits using radiuses bigger than 200
 */
public class SelectionLimiter extends CModule
{
	
	public SelectionLimiter()
	{
		Plugin.get().registerEvents(this);
	}
	
	
	public void onCommandPreProcess(PlayerCommandPreprocessEvent event)
	{
		Player player  = event.getPlayer();
		String message = event.getMessage();
		if(isRadiusTooBig(message))
		{
			event.setCancelled(true);
			plugin.sendMessage(player, "Radius you provided is too big");
		}
		else if(isSelectionTooBig(player))
		{
			event.setCancelled(true);
			plugin.sendMessage(player, "Your selection is too big");
		}
	}
	
	
	private boolean isRadiusTooBig(String command)
	{
		String[] words = command.split(" ");
		for(String word : words)
		{
			// We can safely block each number over 250
			// because blocks have ids below 250
			int number = CUtils.convert(word, Integer.class, 0);
			if(number > 250)
				return true;
		}
		return false;
	}
	
	
	private boolean isSelectionTooBig(Player player)
	{
		LocalSession session = Plugin.worldedit.getSession(player);
		int limit = session.getBlockChangeLimit();
		if(limit == -1)
			return false;
		try
		{
			Region selection = session.getSelection(session.getSelectionWorld());
			return
				selection.getWidth()  > 250
			||  selection.getLength() > 250
			||  selection.getArea()   > limit * 5; 
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