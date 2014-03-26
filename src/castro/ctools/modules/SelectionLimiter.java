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

import castro.ctools.Plugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.regions.Region;


@Deprecated
public class SelectionLimiter extends CModule
{
	
	public SelectionLimiter()
	{
		Plugin.get().registerEvents(this);
	}
	
	
	public void onCommandPreProcess(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		LocalSession session = Plugin.worldedit.getSession(player);
		try
		{
			int limit = session.getBlockChangeLimit();
			Region selection = session.getSelection(session.getSelectionWorld());
			int area = selection.getArea();
			if(limit >= 0)
				if(area > limit * 5)
				{
					event.setCancelled(true);
					plugin.sendMessage(player, "Your selection is too big");
				}
		}
		catch (IncompleteRegionException e)
		{
			// Nothing
		}
	}
	
	
	@Override public boolean isListener()	{ return true; }
	@Override public String[] getCommands()	{ return null; }
}