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

import java.io.FileOutputStream;
import java.util.Calendar;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import castro.ctools.Plugin;

public class Logger extends CModule
{
	private Plugin plugin = Plugin.get();
	
	String path;
	
	
	public Logger(String path)
	{
		this.path = path;
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		checkLog(event.getPlayer(), "command: " + event.getMessage());
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChatEvent(AsyncPlayerChatEvent event)
	{
		checkLog(event.getPlayer(), "chat: " + event.getMessage());
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		checkLog(event.getPlayer(), "joined");
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		checkLog(event.getPlayer(), "left");
	}
	
	
	private void checkLog(Player player, String text)
	{
		if (player.hasPermission("aliquam.mod") || player.hasPermission("castro.log"))
			save(player.getName(), text);
	}
	
	
	private void save(String player, String msg)
	{
		try {
			FileOutputStream fos = new FileOutputStream(path + player, true);
			fos.write((getDate() + " " + msg + "\n").getBytes());
			fos.close();
		} catch (Exception e) { plugin.log("ERROR " + e.getMessage()); }
	}
	
	
	private static String getDate()
	{
		Calendar cal = Calendar.getInstance();
		int tmp; String date;
		tmp = cal.get(Calendar.DAY_OF_MONTH);	date  = ((tmp < 10) ? ("0" + tmp) : (tmp)) + "-";
		tmp = cal.get(Calendar.MONTH) + 1;		date += ((tmp < 10) ? ("0" + tmp) : (tmp)) + "-";
		tmp = cal.get(Calendar.YEAR); 			date += ((tmp < 10) ? ("0" + tmp) : (tmp)) + " ";
		tmp = cal.get(Calendar.HOUR_OF_DAY);	date += ((tmp < 10) ? ("0" + tmp) : (tmp)) + ":";
		tmp = cal.get(Calendar.MINUTE); 		date += ((tmp < 10) ? ("0" + tmp) : (tmp)) + ":";
		tmp = cal.get(Calendar.SECOND); 		date += ( tmp < 10) ? ("0" + tmp) : (tmp);
		return date;
	}


	@Override
	public boolean isListener()
	{
		return true;
	}


	@Override
	public String[] getCommands()
	{
		return null;
	}
}