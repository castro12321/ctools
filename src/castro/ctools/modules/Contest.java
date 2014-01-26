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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import castro.cWorlds.CPlot;
import castro.cWorlds.PlotsMgr;
import castro.ctools.Plugin;
import castro.cWorlds.mapGenerator.MapGenerator;


public class Contest extends CModule
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(sender instanceof Player)
		{
			Player player = (Player)sender;
			String playername = player.getName().toLowerCase();
			
			CPlot plot = PlotsMgr.get(playername, 1000);
			if(plot == null)
				MapGenerator.generate(playername, player, 1000, null); // Set generator to contest
			
			Plugin.dispatchCommand(sender, "plot 1000");
		}
		return false;
	}
	
	
	@Override public boolean isListener()	{ return false; }
	@Override public String[] getCommands()	{ return new String[] {"contest"}; }
}