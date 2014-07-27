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

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import castro.cWorlds.mapGenerator.CallbackPlotCreated;
import castro.cWorlds.plots.CPlot;
import castro.cWorlds.plots.PlotsMgr;
import castro.ctools.Plugin;


public class Contest extends CModule
{
	Player player;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(sender instanceof Player)
		{
			player = (Player)sender;
			String playername = player.getName().toLowerCase();
			
			CPlot plot = PlotsMgr.get(playername, 100);
			if(plot == null)
	            try
                {
	                PlotsMgr.createPlot(playername, 100, onCreate);
                }
                catch(IOException e)
                {
	                e.printStackTrace();
                }
		}
		return false;
	}
	
	private CallbackPlotCreated onCreate = new CallbackPlotCreated()
	{
		@Override
		public void callback(CPlot plot)
		{
			Plugin.dispatchCommand(player, "plot 100");
		}
	};
	
	@Override public boolean isListener()	{ return false; }
	@Override public String[] getCommands()	{ return new String[] {"contest"}; }
}